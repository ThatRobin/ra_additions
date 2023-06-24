package io.github.thatrobin.ra_additions.data;

import io.github.thatrobin.docky.DockyEntry;
import io.github.thatrobin.docky.DockyGenerator;
import io.github.thatrobin.docky.DockyRegistry;
import io.github.thatrobin.docky.providers.*;
import io.github.thatrobin.docky.utils.DataTypeLoader;
import io.github.thatrobin.docky.utils.MkdocsBuilder;
import io.github.thatrobin.docky.utils.PageBuilder;
import io.github.thatrobin.docky.utils.TypeManager;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static io.github.thatrobin.docky.utils.MkdocsBuilder.createPage;
import static io.github.thatrobin.docky.utils.MkdocsBuilder.createSection;

public class DataGener implements DockyGenerator {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        DataTypeLoader.registerApoliDataTypes();

        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        Path outputPath = RA_Additions.getExamplePathRoot();
        if (outputPath != null) {
            pack.addProvider((output, future) -> new DockyReadTheDocsProvider(output, outputPath));
            pack.addProvider((output, future) -> new DockyRequirementsProvider(output, outputPath));

            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "index", "docs", index()));

            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "choice_layer_json", "docs", layerJson()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "choice_json", "docs", choiceJson()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "keybinding_json", "docs", keybindingJson()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "action_json", "docs", actionJson()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "condition_json", "docs", conditionJson()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "tag_extensions", "docs", tagExtensions()));
            pack.addProvider((output, future) -> new DockyPageProvider(output, outputPath, "origin_selectors", "docs", originSelectors()));

            pack.addProvider((output, future) -> new DockyMkDocsProvider(output, outputPath, mkdocs()));

            DataTypeLoader.provideApoliDataTypes(pack, outputPath);

            List<DockyEntry> entries = DockyRegistry.entries();
            entries.sort(Comparator.comparing(a -> a.getFactory().getSerializerId()));
            for (DockyEntry dockyEntry : entries) {
                pack.addProvider((output, future) -> new DockyEntryProvider(output, outputPath, dockyEntry));
            }
            for (String type : TypeManager.getTypes()) {
                pack.addProvider((output, future) -> new DockyTypeContentsPageProvider(output, outputPath, type));
            }
            pack.addProvider(((output, registriesFuture) -> new DockyDataTypeContentsPageProvider(output, outputPath)));
        }
    }

    public PageBuilder index() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Welcome to the documentation for Robin's Apoli Additions!").newLine();
        pageBuilder.addText("This wiki will show you how to use the features and power types in this Apoli Addon.").newLine();
        pageBuilder.addTitle2("Helpful Links")
                .addText("* ", false).addLink("Minecraft Wiki: Data Pack", "https://minecraft.gamepedia.com/Data_Pack")
                .addText("* ", false).addLink("Minecraft Wiki: Creating a Data Pack", "https://minecraft.gamepedia.com/Tutorials/Creating_a_data_pack");
        return pageBuilder;
    }

    public PageBuilder layerJson() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Layer JSON Format");
        pageBuilder.addText("""
                This is the format of a JSON file describing a layer. Layers are collections of choices, and a player can have a single choice on each layer.

                Layer files need to be placed inside the `choice_layers` folder within your namespace.""");

        PageBuilder.TableBuilder tableBuilder = PageBuilder.TableBuilder.init();
        tableBuilder.addRow("Fields", "Type", "Default", "Description")
                .addBreak()
                .addRow("`choices`", "[Array](data_types/array.md) of [Identifiers](data_types/identifier.md)", " ", "Defines the choices that should be in this layer.")
                .addRow("`enabled`", "[Boolean](data_types/boolean.md)", "`true`", "If set to false, this layer will be unavailable.");
        pageBuilder.addTable(tableBuilder);

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/choice_layers/choice_layer_example.json");

        return pageBuilder;
    }

    public PageBuilder choiceJson() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Choice JSON Format");
        pageBuilder.addText("This is the format of a JSON file describing a choice. They need to be placed inside the `choices` folder within your namespace.");

        PageBuilder.TableBuilder tableBuilder = PageBuilder.TableBuilder.init();
        tableBuilder.addRow("Fields", "Type", "Default", "Description")
                .addBreak()
                .addRow("`powers`", "[Array](data_types/array.md) of [Identifiers](data_types/identifier.md)", "_optional_", "IDs of the powers this choice should have.")
                .addRow("`icon`", "[Item Stack](data_types/item_stack.md)", "_optional_", "The item stack which is displayed as the icon for the description button for the choice.")
                .addRow("`name`", "[String](data_types/string.md)", "_optional_", "The display name of the choice.")
                .addRow("`description`", "[String](data_types/string.md)", "_optional_", "The description of the choice.")
                .addRow("`action_on_chosen`", "[Entity Action Type](entity_action_types.md)", "_optional_", "The action that is run when they player selects this choice.");
        pageBuilder.addTable(tableBuilder);

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/choices/choice_example.json");

        return pageBuilder;
    }

    public PageBuilder keybindingJson() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Keybinding JSON Format");
        pageBuilder.addText("This is the format of a JSON file describing a keybinding. They need to be placed inside the `keybinds` folder within your namespace. Keybinds are used to activate active power types, and toggles.");

        PageBuilder.TableBuilder tableBuilder = PageBuilder.TableBuilder.init();
        tableBuilder.addRow("Fields", "Type", "Default", "Description")
                .addBreak()
                .addRow("`key`", "[Key](data_types/key.md)", " ", "A string that defines what key this binding will use.")
                .addRow("`category`", "[String](data_types/string.md)", " ", "The category this key will fall under.");
        pageBuilder.addTable(tableBuilder);

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/keybinds/keybind_example.json");

        return pageBuilder;
    }

    public PageBuilder actionJson() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Action JSON Format");
        pageBuilder.addText("""
                This is the format of a JSON file describing an action. These can be used in place of creating an action inside of a power.\s

                Action JSON files need to be placed inside the `data/<namespace>/{Action Type}` folder of your datapack. Where the `{Action Type}` is either `entity`, `bientity`, `block` or `item`.""");

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/actions/entity/action_example_1.json")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/powers/action_example_2.json");

        return pageBuilder;
    }

    public PageBuilder conditionJson() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Condition JSON Format");
        pageBuilder.addText("""
                This is the format of a JSON file describing a condition. These can be used in place of creating an condition inside of a power.\s

                Condition JSON files need to be placed inside the `data/<namespace>/{Condition Type}` folder of your datapack. Where the `{Condition Type}` is either `entity`, `bientity`, `block` or `item`.""");

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/conditions/entity/condition_example_1.json")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/powers/condition_example_2.json");

        return pageBuilder;
    }

    public PageBuilder tagExtensions() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Tag Extensions");
        pageBuilder.addText("""
                RAA has extended tag functionality into Powers and Actions.

                You can now store your actions, or powers, into tags, and use them with the `/raa` command.
                
                Power Tags can also be used in conjunction with Origins, and also RAA Choices. If you have origins installed, you can make a power tag with the same id as an origin/choice, and the powers specified will be added to the origin/choice, without the need to edit that origin/choice's datapack.""");

        pageBuilder.addTitle3("Examples")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/tags/powers/choice_example.json")
                .addJson(RA_Additions.getExamplePathRoot() + "/testdata/ra_additions/tags/actions/entity/action_tag_example.json");

        return pageBuilder;
    }

    public PageBuilder originSelectors() {
        PageBuilder pageBuilder = PageBuilder.init();

        pageBuilder.addTitle("Origin Selectors");
        pageBuilder.addText("RAA also adds in an origin Selector that can be used in commands, for example `/give @a[origin=origins:human] dirt 64` will give all humans 64 dirt.");

        return pageBuilder;
    }

    public MkdocsBuilder mkdocs() {
        MkdocsBuilder mkdocsBuilder = MkdocsBuilder.init();

        mkdocsBuilder.setName("Robin's Apoli Additions")
                .navigation(
                        createPage("Home", "index.md"),
                        createSection("Types",
                                createPage("Task Types", "task_types.md"),
                                createPage("Power Types", "power_types.md"),
                                createPage("Data Types", "data_types.md")
                        ),
                        createSection("Action Types",
                                createPage("Entity Action Types", "entity_action_types.md"),
                                createPage("Bientity Action Types", "bientity_action_types.md"),
                                createPage("Block Action Types", "block_action_types.md"),
                                createPage("Item Action Types", "item_action_types.md")
                        ),
                        createSection("Condition Types",
                                createPage("Entity Condition Types", "entity_condition_types.md"),
                                createPage("Bientity Condition Types", "bientity_condition_types.md"),
                                createPage("Block Condition Types", "block_condition_types.md"),
                                createPage("Item Condition Types", "item_condition_types.md")
                        ),
                        createSection("JSON",
                                createPage("Action JSON", "action_json.md"),
                                createPage("Condition JSON", "condition_json.md"),
                                createPage("Choice JSON", "choice_json.md"),
                                createPage("Choice Layer JSON", "choice_layer_json.md"),
                                createPage("Keybinding JSON", "keybinding_json.md")
                        ),
                        createSection("Other",
                                createPage("Tag Extensions", "tag_extensions.md"),
                                createPage("Origin Selectors", "origin_selectors.md")
                        )
                )
                .theme();

        return mkdocsBuilder;
    }

}
