package schemely.utils;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;


public class SchemeUtils
{
  public static final String SCHEME_NOTIFICATION_GROUP = "Scheme";

  public static boolean isSuitableModule(Module module)
  {
    if (module == null)
    {
      return false;
    }
    ModuleType type = ModuleTypeManager.getInstance().findByID(module.getModuleTypeName());
    return type instanceof JavaModuleType || "".equals(type.getId());
  }
}
