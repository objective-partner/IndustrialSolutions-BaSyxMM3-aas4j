package org.eclipse.digitaltwin.aas4j.v3.dataformat.xml;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetKind;
import org.eclipse.digitaltwin.aas4j.v3.model.DataTypeDefXsd;
import org.eclipse.digitaltwin.aas4j.v3.model.Extension;
import org.eclipse.digitaltwin.aas4j.v3.model.KeyTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.LangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.aas4j.v3.model.ReferenceTypes;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAdministrativeInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetAdministrationShell.Builder;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultAssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultExtension;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultKey;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangStringNameType;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultReference;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class AasArgumentsProvider implements ArgumentsProvider {

  public static AssetAdministrationShell createNewShell() {
    String testClassName = "AasArgumentsProvider";
    String testMethodName = RandomStringUtils.randomAlphabetic(10);
    String uniqueId = RandomStringUtils.randomAlphabetic(5);
    return new AasArgumentsProvider().prepareAASData(testClassName, testMethodName, uniqueId);
  }

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
    AssetAdministrationShell newAas = prepareAAS(extensionContext);
    Arguments args = Arguments.of(newAas);
    return Stream.of(args);
  }

  protected AssetAdministrationShell prepareAAS(ExtensionContext extensionContext) {
    String testMethodName =
        extensionContext.getTestMethod().map(Method::getName).orElse("unknown_test_name");
    String testClassName =
        extensionContext.getTestClass().map(Class::getSimpleName).orElse("unknown_class_name");
    String uniqueId = UUID.randomUUID().toString();

    return prepareAASData(testClassName, testMethodName, uniqueId);
  }

  public AssetAdministrationShell prepareAASData(
      String testClassName, String testMethodName, String uniqueId) {
    Builder aasBuilder = new Builder();
    return prepareId(testClassName, testMethodName, uniqueId, aasBuilder);
  }

  private AssetAdministrationShell prepareId(
      String testClassName, String testMethodName, String uniqueId, Builder builder) {
    String globalIdentifiableId =
        String.format(
            "%s/%s/%s/%s",
            ArgumentsProviderUtils.getTestIdNameSpace(), testClassName, testMethodName, uniqueId);
    String id = globalIdentifiableId + "/v1";
    builder.id(id);
    return prepareExtensions(testClassName, testMethodName, globalIdentifiableId, builder);
  }

  private AssetAdministrationShell prepareExtensions(
      String testClassName, String testMethodName, String globalIdentifiableId, Builder builder) {
    List<Extension> extensions = new ArrayList<>();
    Reference aas4jDataSpecs =
        new DefaultReference.Builder()
            .keys(new DefaultKey.Builder().type(KeyTypes.BLOB).value("BlobValue").build())
            .type(ReferenceTypes.EXTERNAL_REFERENCE)
            .build();
    DefaultExtension extension =
        new DefaultExtension.Builder()
            .semanticId(aas4jDataSpecs)
            .name("extension")
            .valueType(DataTypeDefXsd.STRING)
            .value("extensionValue")
            .supplementalSemanticIds(aas4jDataSpecs)
            .refersTo(aas4jDataSpecs)
            .build();
    extensions.add(extension);
    builder.extensions(extensions);
    return prepareIdShort(testClassName, testMethodName, builder);
  }

  private AssetAdministrationShell prepareIdShort(
      String testClassName, String testMethodName, Builder builder) {
    String idShort = String.format("%s_%s", testClassName, testMethodName);
    builder.idShort(idShort);
    return prepareDisplayName(testClassName, testMethodName, builder);
  }

  private AssetAdministrationShell prepareDisplayName(
      String testClassName, String testMethodName, Builder builder) {
    String displayNameStr = String.format("%s %s", testClassName, testMethodName);
    List<LangStringNameType> displayName =
        List.of(
            new DefaultLangStringNameType.Builder().text(displayNameStr).language("en").build(),
            new DefaultLangStringNameType.Builder().text(displayNameStr).language("de").build());
    builder.displayName(displayName);
    return prepareAssetInformation(builder);
  }

  private AssetAdministrationShell prepareAssetInformation(Builder builder) {
    DefaultAssetInformation assetInformation =
        new DefaultAssetInformation.Builder()
            .assetKind(AssetKind.INSTANCE)
            .globalAssetId("GLOBAL_ASSET_ID_TESTING")
            .build();
    builder.assetInformation(assetInformation);
    return prepareAdministration(builder);
  }

  private AssetAdministrationShell prepareAdministration(Builder builder) {
    DefaultAdministrativeInformation administration =
        new DefaultAdministrativeInformation.Builder().version("1").revision("33").build();
    builder.administration(administration);
    return buildAAS(builder);
  }

  private DefaultAssetAdministrationShell buildAAS(Builder builder) {
    return builder.build();
  }
}
