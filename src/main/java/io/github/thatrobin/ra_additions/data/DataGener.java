package io.github.thatrobin.ra_additions.data;

import io.github.apace100.apoli.power.factory.Factory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.ClassUtil;
import io.github.apace100.calio.data.ClassDataRegistry;
import io.github.thatrobin.docky.DockyGenerator;
import io.github.thatrobin.docky.utils.DocumentationBuilder;
import io.github.thatrobin.docky.utils.MkdocsBuilder;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactories;
import io.github.thatrobin.ra_additions.powers.factories.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import static io.github.thatrobin.docky.utils.MkdocsBuilder.createPage;
import static io.github.thatrobin.docky.utils.MkdocsBuilder.createSection;

public class DataGener implements DockyGenerator {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        ClassDataRegistry<Factory> factoryRegistry = ClassDataRegistry.getOrCreate(ClassUtil.castClass(Factory.class), "Factory");
        factoryRegistry.addMapping("power", PowerFactories.class);
        factoryRegistry.addMapping("task", GoalFactories.class);
        factoryRegistry.addMapping("action", ActionFactory.class);
        factoryRegistry.addMapping("condition", ConditionFactory.class);

        DocumentationBuilder builder = new DocumentationBuilder();

        builder.outputPath("C:\\Users\\robin\\Desktop\\test");

        builder.mkdocs(mkdocs());

        builder.build();

    }

    public MkdocsBuilder mkdocs() {
        MkdocsBuilder mkdocsBuilder = new MkdocsBuilder();

        mkdocsBuilder.setName("Robin's Apoli Additions")
                .navigation(
                        createPage("Home", "index.md"),
                        createSection("Types",
                                createPage("Task Types", "types/task_types.md"),
                                createPage("Power Types", "types/power_types.md")
                        ),
                        createSection("Condition Types",
                                createPage("Entity Condition Types", "types/entity_condition_types.md"),
                                createPage("Bientity Condition Types", "types/bientity_condition_types.md"),
                                createPage("Block Condition Types", "types/block_condition_types.md"),
                                createPage("Item Condition Types", "types/item_condition_types.md")
                        ),
                        createSection("Action Types",
                                createPage("Entity Action Types", "types/entity_action_types.md"),
                                createPage("Bientity Action Types", "types/bientity_action_types.md"),
                                createPage("Block Action Types", "types/block_action_types.md"),
                                createPage("Item Action Types", "types/item_action_types.md")
                        )
                )
                .theme();
        return mkdocsBuilder;
    }

}
