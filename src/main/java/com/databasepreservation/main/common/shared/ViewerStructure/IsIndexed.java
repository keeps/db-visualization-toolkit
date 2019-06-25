package com.databasepreservation.main.common.shared.ViewerStructure;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * IsIndexed that is compatible with RODA objects, but made to fit DBVTK needs.
 * Unused methods are marked as deprecated.
 *
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public abstract class IsIndexed implements IsSerializable, org.roda.core.data.v2.index.IsIndexed {
  public abstract void setUUID(String uuid);

  @Override
  @Deprecated
  public List<String> toCsvHeaders() {
    return null;
  }

  @Override
  @Deprecated
  public List<Object> toCsvValues() {
    return null;
  }

  @Override
  @Deprecated
  public List<String> liteFields() {
    return null;
  }

  /**
   * Use getUUID instead
   */
  @Override
  @Deprecated
  public String getId() {
    return getUUID();
  }
}