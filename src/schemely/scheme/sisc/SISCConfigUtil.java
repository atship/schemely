package schemely.scheme.sisc;

import com.intellij.openapi.application.PathManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NonNls;

import java.io.File;

/**
 * @author Colin Fleming
 */
public class SISCConfigUtil
{

  public static String getJarPathForResource(Class<?> aClass, String resourceName) {
    String resourceRoot = PathManager.getResourceRoot(aClass, '/' + resourceName);
    return new File(resourceRoot).getAbsoluteFile().getAbsolutePath();
  }
}
