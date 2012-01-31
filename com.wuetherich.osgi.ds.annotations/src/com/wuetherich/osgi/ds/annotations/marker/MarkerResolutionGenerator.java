package com.wuetherich.osgi.ds.annotations.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

  @Override
  public IMarkerResolution[] getResolutions(IMarker marker) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasResolutions(IMarker marker) {
    // TODO Auto-generated method stub
    return false;
  }
}
