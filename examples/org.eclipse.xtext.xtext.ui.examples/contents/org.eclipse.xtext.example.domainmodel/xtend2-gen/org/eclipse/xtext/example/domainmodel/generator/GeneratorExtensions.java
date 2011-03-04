package org.eclipse.xtext.example.domainmodel.generator;

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.example.domainmodel.domainmodel.DomainmodelExtensions;
import org.eclipse.xtext.example.domainmodel.domainmodel.Entity;
import org.eclipse.xtext.example.domainmodel.domainmodel.Operation;
import org.eclipse.xtext.xbase.compiler.ImportManager;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class GeneratorExtensions extends DomainmodelExtensions {
  private final GeneratorExtensions _this = this;
  
  public String shortName(final JvmTypeReference r, final ImportManager importManager) {
    String _xblockexpression = null;
    {
      final StringBuilder builder = new StringBuilder();
      importManager.appendTypeRef(r, builder);
      String _string = builder.toString();
      _xblockexpression = (_string);
    }
    return _xblockexpression;
  }
  
  public String fileName(final Entity e) {
    final Entity typeConverted_e = (Entity)e;
    String _packageName = _this.packageName(typeConverted_e);
    String _replace = _packageName.replace(".", "/");
    String _operator_plus = StringExtensions.operator_plus(_replace, "/");
    String _name = e.getName();
    String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _name);
    String _operator_plus_2 = StringExtensions.operator_plus(_operator_plus_1, ".java");
    return _operator_plus_2;
  }
  
  public String parameterList(final Operation o, final ImportManager importManager) {
    EList<JvmFormalParameter> _params = o.getParams();
    final Function1<JvmFormalParameter,String> function = new Function1<JvmFormalParameter,String>() {
        public String apply(JvmFormalParameter p) {
          JvmTypeReference _parameterType = p.getParameterType();
          String _shortName = _this.shortName(_parameterType, importManager);
          String _operator_plus = StringExtensions.operator_plus(_shortName, " ");
          String _name = p.getName();
          String _operator_plus_1 = StringExtensions.operator_plus(_operator_plus, _name);
          return _operator_plus_1;
        }
      };
    List<String> _map = ListExtensions.map(_params, function);
    String _elementsToString = IterableExtensions.elementsToString(_map, ", ");
    return _elementsToString;
  }
}