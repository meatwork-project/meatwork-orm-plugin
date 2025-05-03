package ${packageName};

import com.meatwork.orm.api.AbstractMeatEntity;
import com.meatwork.orm.api.MetaProperty;
import com.meatwork.orm.api.PropertyType;

public class ${className} extends ${superClass} {

    @Override
    public MetaProperty[] getMetaProperties() {
    <#if superClass == "AbstractMeatEntity">
        return new MetaProperty[] {
        <#list fields as field>
            new MetaProperty(${field.name}, PropertyType.${field.type}, ${field.id}, ${field.unique}, ${field.nullable})
        </#list>
        };
    <#else>
        var metaProp = super.getMetaProperties();
        var newMetaProp = new MetaProperty[] {
        <#list fields as field>
            new MetaProperty(${field.name}, PropertyType.${field.type}, ${field.id}, ${field.unique})
        </#list>
        };
        return Stream.concat(Arrays.stream(metaProp), Arrays.stream(newMetaProp)).toArray(MetaProperty[]::new);
    </#if>

    }

<#list fields as field>
    public ${field.typeConverted} get${field.name?cap_first}() {
        return this.getProperty("${field.name}");
    }

    public void set${field.name?cap_first}(${field.typeConverted} ${field.name}?lower_case) {
        this.updateProperty("${field.name}", ${field.name}?lower_case);
    }
</#list>

    @Override
    public String getTableName() {
        return ${tableName};
    }

}
