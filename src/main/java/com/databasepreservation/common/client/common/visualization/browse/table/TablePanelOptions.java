/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.client.common.visualization.browse.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.databasepreservation.common.client.ObserverManager;
import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.common.RightPanel;
import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.client.common.lists.widgets.MultipleSelectionTablePanel;
import com.databasepreservation.common.client.common.utils.CommonClientUtils;
import com.databasepreservation.common.client.common.utils.JavascriptUtils;
import com.databasepreservation.common.client.configuration.observer.ICollectionStatusObserver;
import com.databasepreservation.common.client.models.status.collection.CollectionStatus;
import com.databasepreservation.common.client.models.status.collection.ColumnStatus;
import com.databasepreservation.common.client.models.structure.ViewerDatabase;
import com.databasepreservation.common.client.models.structure.ViewerForeignKey;
import com.databasepreservation.common.client.models.structure.ViewerPrimaryKey;
import com.databasepreservation.common.client.models.structure.ViewerReference;
import com.databasepreservation.common.client.models.structure.ViewerTable;
import com.databasepreservation.common.client.models.structure.ViewerView;
import com.databasepreservation.common.client.tools.BreadcrumbManager;
import com.databasepreservation.common.client.tools.HistoryManager;
import com.databasepreservation.common.client.widgets.ConfigurationCellTableResources;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.MultiSelectionModel;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class TablePanelOptions extends RightPanel implements ICollectionStatusObserver {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private static final String BTN_SELECT_ALL = "btn-select-all";
  private static Map<String, TablePanelOptions> instances = new HashMap<>();

  public static TablePanelOptions getInstance(CollectionStatus status, ViewerDatabase database, String tableId) {
    String separator = "/";
    String code = database.getUuid() + separator + tableId;
    instances.computeIfAbsent(code, k -> new TablePanelOptions(status, database, tableId));
    return instances.get(code);
  }

  interface TablePanelUiBinder extends UiBinder<Widget, TablePanelOptions> {
  }

  private static TablePanelUiBinder uiBinder = GWT.create(TablePanelUiBinder.class);

  @UiField
  FlowPanel mainHeader;

  @UiField
  FlowPanel mainContainer;

  @UiField
  FlowPanel content;

  @UiField
  Button btnBack;

  @UiField
  Button btnUpdate;

  @UiField
  Button options;

  private ViewerDatabase database;
  private CollectionStatus status;
  private ViewerTable table;
  private boolean allSelected = true; // true: select all; false; select none;
  private boolean showTechnicalInformation = false; // true: show; false: hide;
  private final Map<String, Boolean> initialLoading = new HashMap<>();
  private MultipleSelectionTablePanel<ColumnStatus> columnsTable;
  private Button btnSelectToggle;
  private Label switchLabel;
  private Label labelForSwitch;
  private SimpleCheckBox advancedSwitch;

  private TablePanelOptions(CollectionStatus status, ViewerDatabase viewerDatabase, final String tableID) {
    this.status = status;
    database = viewerDatabase;
    table = database.getMetadata().getTableById(tableID);

    initWidget(uiBinder.createAndBindUi(this));

    ObserverManager.getCollectionObserver().addObserver(this);

    init();
  }

  @Override
  public void handleBreadcrumb(BreadcrumbPanel breadcrumb) {
    BreadcrumbManager.updateBreadcrumb(breadcrumb, BreadcrumbManager.forTable(database.getMetadata().getName(),
      database.getUuid(), table.getNameWithoutPrefix(), table.getId()));
  }

  private Map<String, Boolean> getSelectedColumns() {
    Map<String, Boolean> columnVisibility = new HashMap<>();
    for (ColumnStatus column : status.getTableStatusByTableId(table.getId()).getColumns()) {
      columnVisibility.put(column.getName(), columnsTable.getSelectionModel().isSelected(column));
    }

    return columnVisibility;
  }

  private void init() {
    mainHeader.insert(CommonClientUtils.getHeader(status.getTableStatusByTableId(table.getId()), table, "h1",
      database.getMetadata().getSchemas().size() > 1), 0);
    configureButtons();
    configureTechnicalInformationSwitch();
    initTable();
  }

  private void configureTechnicalInformationSwitch() {
    switchLabel = new Label();
    labelForSwitch = new Label(); // workaround for ie11
    switchLabel.setText(messages.schemaStructurePanelTextForAdvancedOption());
    advancedSwitch = new SimpleCheckBox();

    labelForSwitch.addClickHandler(event -> {
      advancedSwitch.setValue(!advancedSwitch.getValue(), true); // workaround for ie11
      content.clear();
      refreshCellTable(advancedSwitch.getValue());
      showForeignKeyInformation(advancedSwitch.getValue());
      showTriggersInformation(advancedSwitch.getValue());
      showCheckConstraintsInformation(advancedSwitch.getValue());
      showViewInformation(advancedSwitch.getValue());
    });
  }

  private void showCheckConstraintsInformation(boolean value) {
    if (value && table.getCheckConstraints() != null && !table.getCheckConstraints().isEmpty()) {
      content.add(TableCheckConstraintsPanel.getInstance(table));
    }
  }

  private void showTriggersInformation(boolean value) {
    if (value && table.getTriggers() != null && !table.getTriggers().isEmpty()) {
      content.add(TableTriggersPanel.getInstance(table));
    }
  }

  private void showForeignKeyInformation(boolean value) {
    if (value && table.getForeignKeys() != null && !table.getForeignKeys().isEmpty()) {
      content.add(TableForeignKeysPanel.getInstance(database, table));
    }
  }

  private void showViewInformation(boolean value) {
    if (value) {
      for (ViewerView view : database.getMetadata().getSchema(table.getSchemaUUID()).getViews()) {
        if (table.getName().contains(view.getName())
          && table.getName().startsWith(ViewerConstants.MATERIALIZED_VIEW_PREFIX)) {
          content.add(new HTMLPanel(CommonClientUtils.constructViewQuery(view).toSafeHtml()));
        }
      }
      JavascriptUtils.runHighlighter(content.getElement());
    }
  }

  private void configureButtons() {
    btnBack.setText(messages.basicActionBack());

    btnBack.addClickHandler(event -> HistoryManager.gotoTable(database.getUuid(), table.getId()));

    btnUpdate.setText(messages.basicActionUpdate());

    btnUpdate.addClickHandler(event -> {
      ObserverManager.getColumnVisibilityObserver().setCollectionStatus(table.getId(), getSelectedColumns());
      HistoryManager.gotoTable(database.getUuid(), table.getId());
    });

    options.setText(messages.basicActionOptions());

    options.addClickHandler(event -> HistoryManager.gotoTable(database.getUuid(), table.getId()));
  }

  private void initTable() {
    defaultSetSelectAll();
    columnsTable = createCellTableForViewerColumn();
    populateTableColumns(columnsTable);
    content.add(columnsTable);
  }

  private void refreshCellTable(boolean value) {
    showTechnicalInformation = value;
    defaultSetSelectAll();

    columnsTable = createCellTableForViewerColumn();
    populateTableColumns(columnsTable);
    content.add(columnsTable);
  }

  private MultipleSelectionTablePanel<ColumnStatus> createCellTableForViewerColumn() {
    return new MultipleSelectionTablePanel<>(GWT.create(ConfigurationCellTableResources.class));
  }

  private void populateTableColumns(MultipleSelectionTablePanel<ColumnStatus> selectionTablePanel) {
    final ViewerPrimaryKey pk = table.getPrimaryKey();
    final HashSet<Integer> columnIndexesWithForeignKeys = new HashSet<>();
    for (ViewerForeignKey viewerForeignKey : table.getForeignKeys()) {
      for (ViewerReference viewerReference : viewerForeignKey.getReferences()) {
        columnIndexesWithForeignKeys.add(viewerReference.getSourceColumnIndex());
      }
    }

    List<ColumnStatus> collect = status.getTableStatusByTableId(table.getId()).getColumns().stream()
      .filter(p -> p.getSearchStatus().getList().isShow()).sorted().collect(Collectors.toList());

    int size = collect.size();
    Iterator<ColumnStatus> iterator = collect.iterator();

    selectionTablePanel.createTable(getToggleSelectPanel(), new ArrayList<>(), iterator,
      new MultipleSelectionTablePanel.ColumnInfo<>(messages.basicTableHeaderShow(), 4,
        new Column<ColumnStatus, Boolean>(new CheckboxCell(true, true)) {
          @Override
          public Boolean getValue(ColumnStatus viewerColumn) {
            if (initialLoading.get(viewerColumn.getId())) {
              selectionTablePanel.getSelectionModel().setSelected(viewerColumn, true);
              initialLoading.put(viewerColumn.getId(), false);
            } else {
              if (selectionTablePanel.getSelectionModel().getSelectedSet().size() == size) {
                toggleButton(true);
              }

              if (selectionTablePanel.getSelectionModel().getSelectedSet().size() <= 1) {
                toggleButton(false);
              }
            }
            return selectionTablePanel.getSelectionModel().isSelected(viewerColumn);
          }
        }),

      new MultipleSelectionTablePanel.ColumnInfo<>(SafeHtmlUtils.EMPTY_SAFE_HTML, !showTechnicalInformation, 2.2,
        new Column<ColumnStatus, SafeHtml>(new SafeHtmlCell()) {
          @Override
          public SafeHtml getValue(ColumnStatus column) {
            if (pk != null && pk.getColumnIndexesInViewerTable().contains(column.getOrder())) {
              return SafeHtmlUtils.fromSafeConstant("<i class='fa fa-key' title='" + messages.primaryKey() + "'></i>");
            } else if (columnIndexesWithForeignKeys.contains(column.getOrder())) {
              return SafeHtmlUtils.fromSafeConstant(
                "<i class='fa fa-exchange' title='" + messages.foreignKeys_usedByAForeignKeyRelation() + "'></i>");
            } else {
              return SafeHtmlUtils.EMPTY_SAFE_HTML;
            }
          }
        }, "primary-key-col"),

      new MultipleSelectionTablePanel.ColumnInfo<>(messages.tableAndColumnsPageTableHeaderTextForColumnName(), 15,
        new TextColumn<ColumnStatus>() {
          @Override
          public String getValue(ColumnStatus column) {
            return column.getCustomName();
          }
        }),
      new MultipleSelectionTablePanel.ColumnInfo<>(messages.tableAndColumnsPageTableHeaderTextForDescription(), 0,
        new TextColumn<ColumnStatus>() {
          @Override
          public String getValue(ColumnStatus column) {
            return column.getCustomDescription();
          }
        }),
      new MultipleSelectionTablePanel.ColumnInfo<>(messages.tableAndColumnsPageTableHeaderTextForOriginalTypeName(),
        !showTechnicalInformation, 10, new TextColumn<ColumnStatus>() {

          @Override
          public String getValue(ColumnStatus column) {
            return column.getOriginalType();
          }
        }),
      new MultipleSelectionTablePanel.ColumnInfo<>(messages.typeName(), !showTechnicalInformation, 15,
        new TextColumn<ColumnStatus>() {
          @Override
          public String getValue(ColumnStatus column) {
            return column.getTypeName();
          }
        }),

      new MultipleSelectionTablePanel.ColumnInfo<>(messages.nullable(), !showTechnicalInformation, 8,
        new TextColumn<ColumnStatus>() {
          @Override
          public String getValue(ColumnStatus column) {
            if (column.getNullable().equalsIgnoreCase(Boolean.TRUE.toString())) {
              return messages.yes();
            } else {
              return messages.no();
            }
          }
        }));
  }

  private FlowPanel getToggleSelectPanel() {
    btnSelectToggle = new Button();
    btnSelectToggle.setText(messages.basicActionSelectNone());
    btnSelectToggle.addStyleName("btn btn-primary btn-fixed-width btn-select-none");

    btnSelectToggle.addClickHandler(event -> {
      allSelected = !allSelected;
      MultiSelectionModel<ColumnStatus> selectionModel = columnsTable.getSelectionModel();
      for (ColumnStatus column : status.getTableStatusByTableId(table.getId()).getColumns()) {
        selectionModel.setSelected(column, allSelected);
      }

      if (allSelected) {
        btnSelectToggle.setText(messages.basicActionSelectNone());
        btnSelectToggle.removeStyleName(BTN_SELECT_ALL);
        btnSelectToggle.addStyleName("btn-select-none");
      } else {
        btnSelectToggle.setText(messages.basicActionSelectAll());
        btnSelectToggle.removeStyleName("btn-select-none");
        btnSelectToggle.addStyleName(BTN_SELECT_ALL);
      }
    });

    FlowPanel panel = new FlowPanel();
    panel.getElement().getStyle().setProperty("marginTop", 20, Style.Unit.PX);
    panel.add(btnSelectToggle);

    FlowPanel technicalInformation = new FlowPanel();
    technicalInformation.addStyleName("advancedOptionsPanel");
    technicalInformation.add(switchLabel);
    switchLabel.addStyleName("switch-label");
    technicalInformation.add(advancedSwitch);
    advancedSwitch.setStyleName("switch");
    technicalInformation.add(labelForSwitch);
    labelForSwitch.setStyleName("label-for-switch");

    panel.add(technicalInformation);

    return panel;
  }

  private void toggleButton(boolean value) {
    allSelected = value;

    if (allSelected) {
      btnSelectToggle.setText(messages.basicActionSelectNone());
      btnSelectToggle.removeStyleName(BTN_SELECT_ALL);
      btnSelectToggle.addStyleName("btn-select-none");
    } else {
      btnSelectToggle.setText(messages.basicActionSelectAll());
      btnSelectToggle.removeStyleName("btn-select-none");
      btnSelectToggle.addStyleName(BTN_SELECT_ALL);
    }
  }

  private void defaultSetSelectAll() {
    for (ColumnStatus column : status.getTableStatusByTableId(table.getId()).getColumns()) {
      initialLoading.put(column.getId(), true);
    }
  }

  @Override
  public void updateCollection(CollectionStatus collectionStatus) {
    this.status = collectionStatus;
    content.remove(columnsTable);
    refreshCellTable(showTechnicalInformation);
  }
}
