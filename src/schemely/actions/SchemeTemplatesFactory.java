package schemely.actions;

import com.intellij.ide.fileTemplates.*;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.impl.ProjectManagerImpl;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.file.SchemeFileType;

import java.util.Enumeration;
import java.util.Properties;


public class SchemeTemplatesFactory implements FileTemplateGroupDescriptorFactory
{
  @NonNls
  private static final String SCHEME_FILE = "SchemeFile.scm";
  @NonNls
  static final String NAME_TEMPLATE_PROPERTY = "NAME";
  @NonNls
  static final String LOW_CASE_NAME_TEMPLATE_PROPERTY = "lowCaseName";


  public FileTemplateGroupDescriptor getFileTemplatesDescriptor()
  {
    FileTemplateGroupDescriptor
      group =
      new FileTemplateGroupDescriptor(SchemeBundle.message("file.template.group.title.scheme"),
                                      SchemeIcons.SCHEME_ICON);
    group.addTemplate(new FileTemplateDescriptor(SCHEME_FILE, SchemeIcons.SCHEME_ICON));
    return group;
  }

  public static PsiFile createFromTemplate(PsiDirectory directory,
                                           String name,
                                           String fileName,
                                           String templateName,
                                           @NonNls String... parameters) throws IncorrectOperationException
  {
    Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());
//    FileTemplate template = FileTemplateManager.getInstance().getInternalTemplate(templateName);
    /*Properties properties = new Properties(FileTemplateManager.getInstance().getDefaultProperties());
    JavaTemplateUtil.setPackageNameAttribute(properties, directory);
    properties.setProperty(NAME_TEMPLATE_PROPERTY, name);
    properties.setProperty(LOW_CASE_NAME_TEMPLATE_PROPERTY, name.substring(0, 1).toLowerCase() + name.substring(1));
    for (int i = 0; i < parameters.length; i += 2)
    {
      properties.setProperty(parameters[i], parameters[i + 1]);
    }*/
    String text = ";; file " + fileName + " is part of project " + directory.getProject().getName() + "\n;; @author " + properties.getProperty("USER") + "\n;; @date "
            + properties.getProperty("DATE") + ", " + properties.getProperty("TIME")  + "\n\n";
    /*try
    {
      text = template.getText(properties);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Unable to load template for " +
                                 FileTemplateManager.getInstance().internalTemplateToSubject(templateName), e);
    }
*/
    PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
    PsiFile file = factory.createFileFromText(fileName, SchemeFileType.SCHEME_FILE_TYPE, text);
    return (PsiFile) directory.add(file);
  }

}
