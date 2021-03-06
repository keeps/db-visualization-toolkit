/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.client.common.visualization.browse.table;

import com.databasepreservation.common.client.common.RightPanel;
import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.client.common.fields.MetadataField;
import com.databasepreservation.common.client.common.search.SavedSearch;
import com.databasepreservation.common.client.common.search.SearchInfo;
import com.databasepreservation.common.client.common.search.TableSearchPanel;
import com.databasepreservation.common.client.common.utils.CommonClientUtils;
import com.databasepreservation.common.client.configuration.observer.ICollectionStatusObserver;
import com.databasepreservation.common.client.models.status.collection.CollectionStatus;
import com.databasepreservation.common.client.models.structure.ViewerDatabase;
import com.databasepreservation.common.client.services.CollectionService;
import com.databasepreservation.common.client.tools.BreadcrumbManager;
import com.databasepreservation.common.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.client.tools.HistoryManager;
import com.databasepreservation.common.client.tools.ViewerJsonUtils;
import com.databasepreservation.common.client.tools.ViewerStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class TableSavedSearchPanel extends RightPanel {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  public static TableSavedSearchPanel createInstance(ViewerDatabase database, String savedSearchUUID,
    CollectionStatus status) {
    return new TableSavedSearchPanel(database, savedSearchUUID, status);
  }

  interface TableSavedSearchPanelUiBinder extends UiBinder<Widget, TableSavedSearchPanel> {
  }

  private static TableSavedSearchPanelUiBinder uiBinder = GWT.create(TableSavedSearchPanelUiBinder.class);

  private ViewerDatabase database;
  private String savedSearchUUID;
  private SavedSearch savedSearch;
  private CollectionStatus status;

  @UiField
  SimplePanel mainHeader;

  @UiField
  FlowPanel description;

  @UiField
  SimplePanel tableSearchPanelContainer;

  private TableSavedSearchPanel(ViewerDatabase viewerDatabase, final String savedSearchUUID, CollectionStatus status) {
    database = viewerDatabase;
    this.status = status;
    this.savedSearchUUID = savedSearchUUID;

    initWidget(uiBinder.createAndBindUi(this));

    mainHeader.setWidget(
      CommonClientUtils.getHeader(FontAwesomeIconManager.getTag(FontAwesomeIconManager.LOADING), "Loading...", "h1"));

    CollectionService.Util.call((SavedSearch result) -> {
      savedSearch = result;
      init();
    }).retrieveSavedSearch(database.getUuid(), database.getUuid(), savedSearchUUID);
  }

  /**
   * Delegates the method to the innerRightPanel
   *
   * @param breadcrumb
   *          the BreadcrumbPanel for this database
   */
  @Override
  public void handleBreadcrumb(BreadcrumbPanel breadcrumb) {
    BreadcrumbManager.updateBreadcrumb(breadcrumb,
      BreadcrumbManager.forDatabaseSavedSearch(database.getMetadata().getName(), database.getUuid(), savedSearchUUID));
  }

  /**
   * Choose and display the correct panel: a RowPanel when search returns one
   * result, otherwise show a TablePanel
   */
  private void init() {
    String tableId = savedSearch.getTableUUID();

    // set UI
    mainHeader.setWidget(CommonClientUtils.getHeader(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SAVED_SEARCH),
      savedSearch.getName(), "h1"));

    if (ViewerStringUtils.isNotBlank(savedSearch.getDescription())) {
      MetadataField instance = MetadataField.createInstance(savedSearch.getDescription());
      instance.setCSS("table-row-description");
      description.add(instance);
    }

    // set searchForm and table
    SearchInfo searchInfo = ViewerJsonUtils.getSearchInfoMapper().read(savedSearch.getSearchInfoJson());
    if (SearchInfo.isPresentAndValid(searchInfo)) {
      TableSearchPanel tableSearchPanel = new TableSearchPanel(searchInfo.asJson(), status);
      tableSearchPanel.provideSource(database, database.getMetadata().getTableById(tableId));
      tableSearchPanelContainer.setWidget(tableSearchPanel);
    } else {
      GWT.log("search info was invalid. JSON: " + savedSearch.getSearchInfoJson());
    }
  }
}
