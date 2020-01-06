package com.databasepreservation.common.client.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class HistoryManager {
  public static final String ROUTE_HOME = "home";
  public static final String ROUTE_LOGIN = "login";
  public static final String ROUTE_DATABASE = "database";
  public static final String ROUTE_DATABASE_USERS = "users";
  public static final String ROUTE_DATABASE_SEARCH = "search";
  public static final String ROUTE_DATABASE_REPORT = "report";
  public static final String ROUTE_SCHEMA = "schema";
  public static final String ROUTE_SCHEMA_STRUCTURE = "structure";
  public static final String ROUTE_SCHEMA_ROUTINES = "routines";
  public static final String ROUTE_SCHEMA_TRIGGERS = "triggers";
  public static final String ROUTE_SCHEMA_VIEWS = "views";
  public static final String ROUTE_SCHEMA_DATA = "data";
  public static final String ROUTE_TABLE = "table";
  public static final String ROUTE_RECORD = "record";
  public static final String ROUTE_FOREIGN_KEY = "relation";
  public static final String ROUTE_REFERENCES = "references";
  public static final String ROUTE_SCHEMA_CHECK_CONSTRAINTS = "constraints";
  public static final String ROUTE_SAVED_SEARCHES = "searches";
  public static final String ROUTE_SAVED_SEARCHES_EDIT = "edit";
  public static final String ROUTE_UPLOADS = "uploads";
  public static final String ROUTE_UPLOADS_NEW = "upload";
  public static final String ROUTE_SIARD_INFO = "siard";
  public static final String ROUTE_CREATE_SIARD = "create";
  public static final String ROUTE_WIZARD_CONNECTION = "connection";
  public static final String ROUTE_WIZARD_TABLES_COLUMNS = "table-columns";
  public static final String ROUTE_WIZARD_CUSTOM_VIEWS = "custom-views";
  public static final String ROUTE_WIZARD_EXPORT_SIARD_OPTIONS = "siard-export-options";
  public static final String ROUTE_WIZARD_EXPORT_EXT_OPTIONS = "external-lobs";
  public static final String ROUTE_WIZARD_EXPORT_METADATA_OPTIONS = "metadata-export-options";
  public static final String ROUTE_SEND_TO_LIVE_DBMS = "send-to-live-dbms";
  public static final String ROUTE_MIGRATE_TO_SIARD = "migrate-to-siard";
  public static final String ROUTE_WIZARD_EXPORT_FORMAT = "export-format";
  public static final String ROUTE_SIARD_EDIT_METADATA = "metadata";
  public static final String ROUTE_SIARD_EDIT_METADATA_USERS = "users";
  public static final String ROUTE_VIEW = "view";
  public static final String ROUTE_ROUTINE = "routine";
  public static final String ROUTE_SIARD_VALIDATOR = "validator";
  public static final String ROUTE_TABLE_OPTIONS = "options";
  public static final String ROUTE_TABLE_UPDATE = "update";
  public static final String ROUTE_UPLOAD_SIARD_DATA = "ingest-siard";
  public static final String ROUTE_ACTIVITY_LOG = "activity-log";
  public static final String ROUTE_ADVANCED_CONFIGURATION = "advanced-configuration";
  public static final String ROUTE_DATA_TRANSFORMATION = "data-transformation";
  public static final String ROUTE_JOBS = "jobs";

  /****************************************************
   * DESKTOP ROUTES
   ****************************************************/
  public static final String ROUTE_DESKTOP_DATABASE = "desktop-database";
  public static final String ROUTE_DESKTOP_METADATA_TABLE = "desktop-metadata-table";
  public static final String ROUTE_DESKTOP_METADATA_VIEW = "desktop-metadata-view";
  public static final String ROUTE_DESKTOP_METADATA_ROUTINE = "desktop-metadata-routine";
  public static final String ROUTE_DESKTOP_SAVED_SEARCHES = "desktop-searches";
  public static final String ROUTE_DESKTOP_SCHEMA = "desktop-schema";
  public static final String ROUTE_DESKTOP_TABLE = "desktop-table";

  public static final String HISTORY_SEP = "/";
  public static final String HISTORY_SEP_REGEX = "/";
  public static final String HISTORY_SEP_ESCAPE = "%2F";

  public static final String ACTION_DELETE = "delete";

  public static void gotoRoot() {
    newHistory(Collections.singletonList(ROUTE_HOME));
  }

  public static void returnFromLogin() {
    List<String> currentPath = getCurrentHistoryPath();
    List<String> loginPath = pathLogin();

    // check if the current path starts with the login path
    if (currentPath.size() > 1 && currentPath.size() > loginPath.size()
        && currentPath.subList(0, loginPath.size()).equals(loginPath)) {
      newHistory(currentPath.subList(1, currentPath.size()));
    } else {
      // in case something is wrong or has been tampered with, go to root
      gotoHome();
    }
  }

  public static void gotoLogin() {
    newHistory(pathLogin());
  }

  private static List<String> pathLogin() {
    return Collections.singletonList(ROUTE_LOGIN);
  }

  public static void gotoDatabaseList() {
    newHistory(Collections.singletonList(ROUTE_DATABASE));
  }

  public static void gotoHome() {
    gotoRoot();
  }

  public static void gotoNewUpload() {
    newHistory(Arrays.asList(ROUTE_UPLOADS));
  }

  public static void gotoDatabase() {
    newHistory(Collections.singletonList(ROUTE_DATABASE));
  }


  public static void gotoDatabase(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_DATABASE, databaseUUID));
  }

  public static void gotoDatabaseUsers(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_DATABASE, databaseUUID, ROUTE_DATABASE_USERS));
  }

  public static void gotoDatabaseSearch(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_DATABASE, databaseUUID, ROUTE_DATABASE_SEARCH));
  }

  public static void gotoDatabaseReport(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_DATABASE, databaseUUID, ROUTE_DATABASE_REPORT));
  }

  public static void gotoSchema(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID));
  }

  public static void gotoSchemaStructure(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_STRUCTURE));
  }

  public static void gotoSchemaRoutines(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_ROUTINES));
  }

  public static void gotoSchemaRoutines(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, ROUTE_SCHEMA_ROUTINES));
  }

  public static void gotoSchemaTriggers(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_TRIGGERS));
  }

  public static void gotoSchemaViews(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_VIEWS));
  }

  public static void gotoSchemaData(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_DATA));
  }

  public static void gotoSchemaCheckConstraints(String databaseUUID, String schemaUUID) {
    newHistory(Arrays.asList(ROUTE_SCHEMA, databaseUUID, schemaUUID, ROUTE_SCHEMA_CHECK_CONSTRAINTS));
  }

  public static void gotoSavedSearches(String databaseUUID) {
    newHistory(Arrays.asList(ROUTE_SAVED_SEARCHES, databaseUUID));
  }

  public static void gotoSavedSearch(String databaseUUID, String savedSearchUUID) {
    newHistory(Arrays.asList(ROUTE_SAVED_SEARCHES, databaseUUID, savedSearchUUID));
  }

  public static void gotoEditSavedSearch(String databaseUUID, String savedSearchUUID) {
    newHistory(Arrays.asList(ROUTE_SAVED_SEARCHES, databaseUUID, savedSearchUUID, ROUTE_SAVED_SEARCHES_EDIT));
  }

  public static void gotoTable(String databaseUUID, String tableUUID) {
    newHistory(Arrays.asList(ROUTE_TABLE, databaseUUID, tableUUID));
  }

  public static void gotoTableUpdate(String databaseUUID, String tableUUID) {
    newHistory(Arrays.asList(ROUTE_TABLE, databaseUUID, tableUUID, ROUTE_TABLE_UPDATE));
  }

  public static void gotoTableOptions(String databaseUUID, String tableUUID) {
    newHistory(Arrays.asList(ROUTE_TABLE, databaseUUID, tableUUID, ROUTE_TABLE_OPTIONS));
  }

  public static void gotoRelationOptions(String databaseUUID, String tableUUID, List<String> searchInfo) {
    List<String> params = new ArrayList<>(Arrays.asList(ROUTE_FOREIGN_KEY, databaseUUID, tableUUID));
    params.addAll(searchInfo);
    params.add(ROUTE_TABLE_OPTIONS);
    newHistory(params);
  }

  public static void gotoForeignKeyUpdate(String databaseUUID, String tableUUID, List<String> searchInfo) {
    List<String> params = new ArrayList<>(Arrays.asList(ROUTE_FOREIGN_KEY, databaseUUID, tableUUID));
    params.addAll(searchInfo);
    params.add(ROUTE_TABLE_UPDATE);
    newHistory(params);
  }

  public static void gotoView(String databaseUUID, String viewUUID) {
    newHistory(Arrays.asList(ROUTE_VIEW, databaseUUID, viewUUID));
  }

  public static void gotoViewOptions(String databaseUUID, String tableUUID) {
    newHistory(Arrays.asList(ROUTE_VIEW, databaseUUID, tableUUID, ROUTE_TABLE_OPTIONS));
  }

  public static void gotoRecord(String databaseUUID, String tableUUID, String recordUUID) {
    newHistory(Arrays.asList(ROUTE_RECORD, databaseUUID, tableUUID, recordUUID));
  }

  public static void gotoReferences(String databaseUUID, String tableUUID, String recordUUID,
                                    String columnIndexInTable) {
    newHistory(Arrays.asList(ROUTE_REFERENCES, databaseUUID, tableUUID, recordUUID, columnIndexInTable));
  }

  public static void gotoForeignKey(String databaseUUID, String tableUUID, List<String> solrColumnsAndValues) {
    List<String> params = new ArrayList<>(Arrays.asList(ROUTE_FOREIGN_KEY, databaseUUID, tableUUID));
    params.addAll(solrColumnsAndValues);
    newHistory(params);
  }

  public static void gotoSIARDInfo(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_SIARD_INFO, databaseUUID);
    newHistory(params);
  }

  public static void gotoCreateSIARD() {
    List<String> params = Collections.singletonList(ROUTE_CREATE_SIARD);
    newHistory(params);
  }

  public static void gotoIngestSIARDData(final String databaseUUID, final String databaseName) {
    List<String> params = Arrays.asList(ROUTE_UPLOAD_SIARD_DATA, databaseUUID, databaseName);
    newHistory(params);
  }

  public static void gotoCreateSIARDErDiagram(String wizardPage, String toSelect, String schemaUUID, String tableUUID) {
    List<String> params = Arrays.asList(ROUTE_CREATE_SIARD, wizardPage, toSelect, schemaUUID, tableUUID);
    newHistory(params);
  }

  public static void gotoSIARDEditMetadata(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_SIARD_EDIT_METADATA, databaseUUID);
    newHistory(params);
  }

  public static void gotoSendToLiveDBMS(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_SEND_TO_LIVE_DBMS, databaseUUID);
    newHistory(params);
  }

  public static void gotoSendToLiveDBMSExportFormat(String databaseUUID, String databaseName) {
    List<String> params = Arrays.asList(ROUTE_SEND_TO_LIVE_DBMS, databaseUUID, databaseName);
    newHistory(params);
  }

  public static void gotoMigrateSIARD(String databaseUUID, String databaseName) {
    List<String> params = Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, databaseName);
    newHistory(params);
  }

  public static void gotoSendToLiveDBMSExportFormatErDiagram(String databaseUUID, String wizardPage, String toSelect,
                                                             String schemaUUID, String tableUUID) {
    List<String> params = Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, wizardPage, toSelect, schemaUUID,
        tableUUID);
    newHistory(params);
  }

  public static void gotoEditMetadataUsers(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_SIARD_EDIT_METADATA, databaseUUID, ROUTE_SIARD_EDIT_METADATA_USERS);
    newHistory(params);
  }

  public static void gotoSIARDValidator(String databaseUUID, String reporterPath, String udtPath, String skipAdditionalChecks) {
    List<String> params = Arrays.asList(ROUTE_SIARD_VALIDATOR, databaseUUID, reporterPath, udtPath, skipAdditionalChecks);
    newHistory(params);
  }

  public static void gotoSIARDValidator(String databaseUUID, String reporterPath, String skipAdditionalChecks) {
    List<String> params = Arrays.asList(ROUTE_SIARD_VALIDATOR, databaseUUID, reporterPath, skipAdditionalChecks);
    newHistory(params);
  }

  public static void gotoSIARDValidator(String databaseUUID, String skipAdditionalChecks) {
    List<String> params = Arrays.asList(ROUTE_SIARD_VALIDATOR, databaseUUID, skipAdditionalChecks);
    newHistory(params);
  }

  public static void gotoActivityLog() {
    List<String> params = Collections.singletonList(ROUTE_ACTIVITY_LOG);
    newHistory(params);
  }

  public static void gotoActivityLog(final String logUUID) {
    List<String> params = Arrays.asList(ROUTE_ACTIVITY_LOG, logUUID);
    newHistory(params);
  }

  public static void gotoAdvancedConfiguration(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_ADVANCED_CONFIGURATION, databaseUUID);
    newHistory(params);
  }

  public static void gotoDataTransformation(String databaseUUID) {
    List<String> params = Arrays.asList(ROUTE_DATA_TRANSFORMATION, databaseUUID);
    newHistory(params);
  }

  public static void gotoDataTransformation(String databaseUUID, String tableUUID) {
    List<String> params = Arrays.asList(ROUTE_DATA_TRANSFORMATION, databaseUUID, tableUUID);
    newHistory(params);
  }

  public static void gotoJobs() {
    List<String> params = Arrays.asList(ROUTE_JOBS);
    newHistory(params);
  }

  private static void newHistory(List<String> path) {
    // History.newItem(createHistoryToken(path)
    String hash = createHistoryToken(path);
    Window.Location.assign("#" + hash);
  }

  private static String createHistoryToken(List<String> tokens) {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (String token : tokens) {
      if (first) {
        first = false;
      } else {
        builder.append(HISTORY_SEP);
      }

      String encodedToken = URL.encode(token).replaceAll(HISTORY_SEP_REGEX, HISTORY_SEP_ESCAPE);
      builder.append(encodedToken);
    }

    return builder.toString();
  }

  public static List<String> getCurrentHistoryPath() {
    List<String> tokens = new ArrayList<>();

    String hash = Window.Location.getHash();
    if (hash.length() > 0) {
      hash = hash.substring(1);
      String[] split = hash.split(HISTORY_SEP_REGEX);
      for (String item : split) {
        tokens.add(URL.decode(item));
      }
    }

    return tokens;
  }

  public static String linkToReferences(String database_uuid, String table_uuid, String record_uuid,
                                        String columnIndexInTable) {
    return createHistoryToken(
        Arrays.asList(ROUTE_REFERENCES, database_uuid, table_uuid, record_uuid, columnIndexInTable));
  }

  public static String linkToRecord(String database_uuid, String table_uuid, String record_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_RECORD, database_uuid, table_uuid, record_uuid));
  }

  public static String linkToLogin() {
    return createHistoryToken(pathLogin());
  }

  public static String linkToTable(String database_uuid, String table_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_TABLE, database_uuid, table_uuid));
  }

  public static String linkToView(String database_uuid, String viewUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_VIEW, database_uuid, viewUUID));
  }

  public static String linkToDesktopMetadataTable(String database_uuid, String table_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DESKTOP_METADATA_TABLE, database_uuid, table_uuid));
  }

  public static String linTokDesktopMetadataView(String database_uuid, String schema_uuid, String view_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DESKTOP_METADATA_VIEW, database_uuid, schema_uuid, view_uuid));
  }

  public static String linkToDesktopMetadataRoutine(String database_uuid, String schema_uuid, String routine_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DESKTOP_METADATA_ROUTINE, database_uuid, schema_uuid, routine_uuid));
  }

  public static String linkToDatabase(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATABASE, database_uuid));
  }

  public static String linkToDatabaseUsers(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATABASE, database_uuid, ROUTE_DATABASE_USERS));
  }

  public static String linkToDatabaseReport(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATABASE, database_uuid, ROUTE_DATABASE_REPORT));
  }

  public static String linkToDatabaseSearch(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATABASE, database_uuid, ROUTE_DATABASE_SEARCH));
  }

  public static String linkToSchemaRoutines(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATABASE, database_uuid, ROUTE_SCHEMA_ROUTINES));
  }

  public static String linkToSavedSearches(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_SAVED_SEARCHES, database_uuid));
  }

  public static String linkToSavedSearch(String databaseUUID, String savedSearchUUID) {
      return createHistoryToken(Arrays.asList(ROUTE_SAVED_SEARCHES, databaseUUID, savedSearchUUID));
  }

  public static String linkToForeignKey(String database_uuid, String table_uuid, List<String> solrColumnsAndValues) {
    List<String> params = new ArrayList<>(Arrays.asList(ROUTE_FOREIGN_KEY, database_uuid, table_uuid));
    params.addAll(solrColumnsAndValues);
    return createHistoryToken(params);
  }

  public static String linkToDatabaseMetadata(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_SIARD_EDIT_METADATA, database_uuid));
  }

  public static String linkToDatabaseMetadataUsers(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_SIARD_EDIT_METADATA, database_uuid, ROUTE_SIARD_EDIT_METADATA_USERS));
  }

  public static String linkToCreateSIARD(String wizardPage, String toSelect) {
    return createHistoryToken(Arrays.asList(ROUTE_CREATE_SIARD, wizardPage, toSelect));
  }

  public static String linkToCreateSIARD(String wizardPage, String toSelect, String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_CREATE_SIARD, wizardPage, toSelect, database_uuid));
  }

  public static String linkToCreateSIARD(String wizardPage, String toSelect, String database_uuid, String table_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_CREATE_SIARD, wizardPage, toSelect, database_uuid, table_uuid));
  }

  public static String linkToCreateWizardCustomViewsSelect(String customViewID) {
    return createHistoryToken(Arrays.asList(ROUTE_CREATE_SIARD, ROUTE_WIZARD_CUSTOM_VIEWS, customViewID));
  }

  public static String linkToCreateWizardCustomViewsDelete(String customViewID) {
    return createHistoryToken(Arrays.asList(ROUTE_CREATE_SIARD, ROUTE_WIZARD_CUSTOM_VIEWS, customViewID, ACTION_DELETE));
  }

  public static String linkToSendToWizardTableAndColumnsShowTables(String toSelect, String databaseUUID, String tableUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, ROUTE_WIZARD_TABLES_COLUMNS, toSelect, tableUUID));
  }

  public static String linkToSendToWizardTableAndColumnsShowColumns(String toSelect, String databaseUUID, String schemaUUID, String tableUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, ROUTE_WIZARD_TABLES_COLUMNS, toSelect, schemaUUID, tableUUID));
  }

  public static String linkToSendToWizardTableAndColumnsShowViews(String toSelect, String databaseUUID, String schemaUUID, String viewUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, ROUTE_WIZARD_TABLES_COLUMNS, toSelect, schemaUUID, viewUUID));
  }

  public static String linkToSendToWizardTableAndColumnsShowERDiagram(String toSelect, String databaseUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_MIGRATE_TO_SIARD, databaseUUID, ROUTE_WIZARD_TABLES_COLUMNS, toSelect));
  }

  public static String linkToSendToWizardDBMSConnection(String databaseUUID, String wizardPage, String moduleName) {
    return createHistoryToken(Arrays.asList(ROUTE_SEND_TO_LIVE_DBMS, databaseUUID, wizardPage, moduleName));
  }

  public static String linkToLog(String logUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_ACTIVITY_LOG, logUUID));
  }

  public static String linkToSIARDInfo(String databaseUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_SIARD_INFO, databaseUUID));
  }

  public static String linkToDataTransformation(String database_uuid) {
    return createHistoryToken(Arrays.asList(ROUTE_DATA_TRANSFORMATION, database_uuid));
  }

  public static String linkToDataTransformationTable(String database_uuid, String tableUUID) {
    return createHistoryToken(Arrays.asList(ROUTE_DATA_TRANSFORMATION, database_uuid, tableUUID));
  }
}
