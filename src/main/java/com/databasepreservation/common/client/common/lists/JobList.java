package com.databasepreservation.common.client.common.lists;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.databasepreservation.common.api.v1.JobResource;
import com.databasepreservation.common.client.models.DataTransformationProgressData;
import org.fusesource.restygwt.client.MethodCallback;
import com.databasepreservation.common.client.index.filter.Filter;
import org.roda.core.data.v2.index.sublist.Sublist;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.common.lists.columns.TooltipColumn;
import com.databasepreservation.common.client.common.lists.utils.BasicAsyncTableCell;
import com.databasepreservation.common.client.common.utils.html.LabelUtils;
import com.databasepreservation.common.client.index.FindRequest;
import com.databasepreservation.common.client.index.IndexResult;
import com.databasepreservation.common.client.index.facets.Facets;
import com.databasepreservation.common.client.index.sort.Sorter;
import com.databasepreservation.common.client.models.structure.ViewerJob;
import com.databasepreservation.common.client.services.JobService;
import com.databasepreservation.common.client.tools.Humanize;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;

import config.i18n.client.ClientMessages;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class JobList extends BasicAsyncTableCell<ViewerJob> {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  private Column<ViewerJob, SafeHtml> idColumn;
  private Column<ViewerJob, SafeHtml> databaseColumn;
  private Column<ViewerJob, SafeHtml> tableColumn;
  private Column<ViewerJob, SafeHtml> nameColumn;
  private Column<ViewerJob, SafeHtml> createTimeColumn;
  private Column<ViewerJob, SafeHtml> startTimeColumn;
  private Column<ViewerJob, SafeHtml> endTimeColumn;
  private Column<ViewerJob, SafeHtml> progressColumn;
  private Column<ViewerJob, SafeHtml> statusColumn;
  private Column<ViewerJob, SafeHtml> detailColumn;

  public JobList() {
    this(new Filter(), null, null, false, false);
  }

  private JobList(Filter filter, Facets facets, String summary, boolean selectable, boolean exportable) {
    super(filter, facets, summary, selectable, exportable, 15, 15);
    autoUpdate(5000);
  }

  @Override
  protected void configureDisplay(CellTable<ViewerJob> display) {

    idColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getJobId() != null
          ? SafeHtmlUtils.fromString(viewerJob.getJobId().toString())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    databaseColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getDatabaseName() != null
          ? SafeHtmlUtils.fromString(viewerJob.getDatabaseName())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    tableColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getTableName() != null
          ? SafeHtmlUtils.fromString(viewerJob.getTableName())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    nameColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getName() != null ? SafeHtmlUtils.fromString(viewerJob.getName())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    createTimeColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getCreateTime() != null
          ? SafeHtmlUtils.fromString(Humanize.formatDateTime(viewerJob.getStartTime()))
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    startTimeColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getStartTime() != null
          ? SafeHtmlUtils.fromString(Humanize.formatDateTime(viewerJob.getStartTime()))
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    endTimeColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        if (viewerJob != null && viewerJob.getEndTime() != null) {
          return SafeHtmlUtils.fromString(Humanize.formatDateTime(viewerJob.getEndTime()));
        } else {
          return SafeHtmlUtils.fromString("unknown");
        }
      }
    };

    progressColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        if (viewerJob != null && viewerJob.getProcessRows() != null && viewerJob.getRowsToProcess() != null) {
          int currentGlobalPercent = new Double(
            (viewerJob.getProcessRows() * 1.0D / viewerJob.getRowsToProcess()) * 100).intValue();

          return SafeHtmlUtils.fromString(
            currentGlobalPercent + "% (" + viewerJob.getProcessRows() + " of " + viewerJob.getRowsToProcess() + ")");
        } else {
          return SafeHtmlUtils.fromString("0%");
        }
      }
    };

    statusColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getStatus() != null ? LabelUtils.getJobStatus(viewerJob.getStatus())
          : SafeHtmlUtils.fromString("unknown");
      }
    };

    detailColumn = new TooltipColumn<ViewerJob>() {
      @Override
      public SafeHtml getValue(ViewerJob viewerJob) {
        return viewerJob != null && viewerJob.getExitDescription() != null
          ? SafeHtmlUtils.fromString(viewerJob.getExitDescription())
          : SafeHtmlUtils.fromString("");
      }
    };

    addColumn(idColumn, messages.batchJobsTextForJobId(), true, TextAlign.NONE, 5);
    addColumn(databaseColumn, messages.batchJobsTextForDatabase(), true, TextAlign.NONE, 10);
    addColumn(tableColumn, messages.batchJobsTextForTable(), true, TextAlign.NONE, 10);
    addColumn(nameColumn, messages.batchJobsTextForName(), true, TextAlign.NONE, 8);
    addColumn(createTimeColumn, messages.batchJobsTextForCreateTime(), true, TextAlign.NONE, 10);
    addColumn(startTimeColumn, messages.batchJobsTextForStartTime(), true, TextAlign.NONE, 10);
    addColumn(endTimeColumn, messages.batchJobsTextForEndTime(), true, TextAlign.NONE, 10);
    addColumn(progressColumn, "Progress", true, TextAlign.NONE, 10);
    addColumn(statusColumn, messages.batchJobsTextForStatus(), true, TextAlign.NONE, 8);
    addColumn(detailColumn, messages.batchJobsTextForDetail(), true, TextAlign.NONE, 30);

    idColumn.setSortable(true);
    statusColumn.setSortable(true);

    display.getColumnSortList().push(new ColumnSortList.ColumnSortInfo(idColumn, false));
  }

  @Override
  protected void getData(Sublist sublist, ColumnSortList columnSortList,
    MethodCallback<IndexResult<ViewerJob>> callback) {
    Filter filter = getFilter();

    Map<Column<ViewerJob, ?>, List<String>> columnSortingKeyMap = new HashMap<>();
    columnSortingKeyMap.put(idColumn, Collections.singletonList(ViewerConstants.SOLR_BATCH_JOB_ID));
    columnSortingKeyMap.put(statusColumn, Collections.singletonList(ViewerConstants.SOLR_BATCH_JOB_STATUS));

    Sorter sorter = createSorter(columnSortList, columnSortingKeyMap);
    FindRequest findRequest = new FindRequest(ViewerJob.class.getName(), filter, sorter, sublist, getFacets());

    JobService.Util.call(callback).find(findRequest, LocaleInfo.getCurrentLocale().getLocaleName());
  }

  @Override
  public void exportClickHandler() {
    // do nothing
  }

  @Override
  protected void onAttach() {
    super.onAttach();
    refresh();
  }

//  @Override
//  public void refresh() {
//    super.refresh();
//
//    JobResource.Util.call((HashMap<String, DataTransformationProgressData> progressDataMap) -> {
//      GWT.log("JobResource::");
//      progressColumn = new TooltipColumn<ViewerJob>() {
//        @Override
//        public SafeHtml getValue(ViewerJob viewerJob) {
//          DataTransformationProgressData progressData = progressDataMap.get(viewerJob.getUuid());
//          long rowToProcess;
//          long processedRows;
//          if (viewerJob.getEndTime() == null && progressData != null) {
//            rowToProcess = progressData.getRowsToProcess();
//            processedRows = progressData.getProcessedRows();
//          } else {
//            rowToProcess = viewerJob.getRowsToProcess();
//            processedRows = viewerJob.getProcessRows();
//          }
//          int currentGlobalPercent = new Double((processedRows * 1.0D / rowToProcess) * 100).intValue();
//          GWT.log("currentGlobalPercent::" + currentGlobalPercent);
//          return SafeHtmlUtils.fromString(currentGlobalPercent + "% (" + processedRows + " of " + rowToProcess + ")");
//        }
//      };
//
//    }).getProgress();
//
//  }
}
