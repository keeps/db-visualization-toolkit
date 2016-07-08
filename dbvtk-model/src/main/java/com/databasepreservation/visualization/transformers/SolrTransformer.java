package com.databasepreservation.visualization.transformers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.databasepreservation.visualization.ViewerConstants;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerCell;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerDatabase;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerMetadata;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerRow;
import com.databasepreservation.visualization.client.ViewerStructure.ViewerTable;
import com.databasepreservation.visualization.exceptions.ViewerException;
import com.databasepreservation.visualization.utils.ViewerUtils;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class SolrTransformer {
  private static final Logger LOGGER = LoggerFactory.getLogger(SolrTransformer.class);

  /**
   * Private empty constructor
   */
  private SolrTransformer() {
  }

  /***********************************************************************************
   * Database viewer structures to solr documents
   **********************************************************************************/

  public static SolrInputDocument fromDatabase(ViewerDatabase viewerDatabase) throws ViewerException {
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField(ViewerConstants.SOLR_DATABASE_ID, viewerDatabase.getUUID());
    doc.addField(ViewerConstants.SOLR_DATABASE_METADATA, metadataAsJsonString(viewerDatabase.getMetadata()));
    return doc;
  }

  private static String metadataAsJsonString(ViewerMetadata viewerMetadata) throws ViewerException {
    return JsonTransformer.getJsonFromObject(viewerMetadata);
  }

  public static SolrInputDocument fromRow(ViewerTable table, ViewerRow row) throws ViewerException {
    SolrInputDocument doc = new SolrInputDocument();

    for (Map.Entry<String, ViewerCell> cellEntry : row.getCells().entrySet()) {
      String solrColumnName = cellEntry.getKey();
      String cellValue = cellEntry.getValue().getValue();

      doc.addField(solrColumnName, cellValue);
    }

    doc.setField(ViewerConstants.SOLR_ROW_ID, row.getUUID());
    return doc;
  }

  /***********************************************************************************
   * Solr documents to Database viewer structures
   **********************************************************************************/

  public static ViewerDatabase toDatabase(SolrDocument doc) throws ViewerException {
    ViewerDatabase viewerDatabase = new ViewerDatabase();
    viewerDatabase.setUuid(objectToString(doc.get(ViewerConstants.SOLR_DATABASE_ID)));
    viewerDatabase.setMetadata(toMetadata(doc));
    return viewerDatabase;
  }

  private static ViewerMetadata toMetadata(SolrDocument doc) throws ViewerException {
    String metadataAsJsonString = objectToString(doc.get(ViewerConstants.SOLR_DATABASE_METADATA));
    return JsonTransformer.getObjectFromJson(metadataAsJsonString, ViewerMetadata.class);
  }

  public static ViewerRow toRow(SolrDocument doc) {
    ViewerRow viewerRow = new ViewerRow();
    viewerRow.setUUID(objectToString(doc.get(ViewerConstants.SOLR_ROW_ID)));

    HashMap<String, ViewerCell> cells = new HashMap<>();
    for (String columnName : doc.keySet()) {
      if (columnName.startsWith(ViewerConstants.SOLR_INDEX_ROW_COLUMN_NAME_PREFIX)) {
        ViewerCell viewerCell = new ViewerCell();
        viewerCell.setValue(doc.get(columnName).toString());
        cells.put(columnName, viewerCell);
      }
    }
    viewerRow.setCells(cells);

    return viewerRow;
  }

  private static String objectToString(Object object) {
    String ret;
    if (object == null) {
      ret = null;
    } else if (object instanceof String) {
      ret = (String) object;
    } else {
      LOGGER.warn("Could not convert Solr object to string, unsupported class: {}", object.getClass().getName());
      ret = object.toString();
    }
    return ret;
  }

  private static List<String> objectToListString(Object object) {
    List<String> ret;
    if (object == null) {
      ret = new ArrayList<String>();
    } else if (object instanceof String) {
      List<String> l = new ArrayList<String>();
      l.add((String) object);
      return l;
    } else if (object instanceof List<?>) {
      List<?> l = (List<?>) object;
      ret = new ArrayList<String>();
      for (Object o : l) {
        ret.add(o.toString());
      }
    } else {
      LOGGER.error("Could not convert Solr object to List<String> ({})", object.getClass().getName());
      ret = new ArrayList<String>();
    }
    return ret;
  }

  public static Integer objectToInteger(Object object, Integer defaultValue) {
    Integer ret;
    if (object instanceof Integer) {
      ret = (Integer) object;
    } else if (object instanceof String) {
      try {
        ret = Integer.parseInt((String) object);
      } catch (NumberFormatException e) {
        LOGGER.error("Could not convert Solr object to integer", e);
        ret = defaultValue;
      }
    } else {
      LOGGER.error("Could not convert Solr object to integer ({})", object.getClass().getName());
      ret = defaultValue;
    }
    return ret;
  }

  public static Long objectToLong(Object object, Long defaultValue) {
    Long ret = defaultValue;
    if (object != null) {
      if (object instanceof Long) {
        ret = (Long) object;
      } else if (object instanceof String) {
        try {
          ret = Long.parseLong((String) object);
        } catch (NumberFormatException e) {
          LOGGER.error("Could not convert Solr object to long", e);
          ret = defaultValue;
        }
      } else {
        LOGGER.error("Could not convert Solr object to long ({})", object.getClass().getName());
        ret = defaultValue;
      }
    }
    return ret;
  }

  private static Float objectToFloat(Object object) {
    Float ret;
    if (object instanceof Float) {
      ret = (Float) object;
    } else if (object instanceof String) {
      try {
        ret = Float.parseFloat((String) object);
      } catch (NumberFormatException e) {
        LOGGER.error("Could not convert Solr object to float", e);
        ret = null;
      }
    } else {
      LOGGER.error("Could not convert Solr object to float ({})", object.getClass().getName());
      ret = null;
    }
    return ret;
  }

  private static Date objectToDate(Object object) {
    Date ret;
    if (object == null) {
      ret = null;
    } else if (object instanceof Date) {
      ret = (Date) object;
    } else if (object instanceof String) {
      try {
        LOGGER.trace("Parsing date ({}) from string", object);
        ret = ViewerUtils.parseDate((String) object);
      } catch (ParseException e) {
        LOGGER.error("Could not convert Solr object to date", e);
        ret = null;
      }
    } else {
      LOGGER.error("Could not convert Solr object to date, unsupported class: {}", object.getClass().getName());
      ret = null;
    }

    return ret;
  }

  private static Boolean objectToBoolean(Object object) {
    return objectToBoolean(object, null);
  }

  private static Boolean objectToBoolean(Object object, Boolean defaultValue) {
    Boolean ret;
    if (object == null) {
      ret = defaultValue;
    } else if (object instanceof Boolean) {
      ret = (Boolean) object;
    } else if (object instanceof String) {
      ret = Boolean.parseBoolean((String) object);
    } else {
      LOGGER.error("Could not convert Solr object to Boolean ({})", object.getClass().getName());
      ret = null;
    }
    return ret;
  }
}