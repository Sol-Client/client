package me.mcblueparrot.client.gradle;

import net.minecraftforge.gradle.user.ReobfMappingType;
import net.minecraftforge.gradle.user.ReobfTaskFactory;
import net.minecraftforge.gradle.user.tweakers.ClientTweaker;

public class CustomTweakerPlugin extends ClientTweaker {

    @Override
    protected void setupReobf(ReobfTaskFactory.ReobfTaskWrapper reobf) {
        super.setupReobf(reobf);
        reobf.setMappingType(ReobfMappingType.SEARGE);
    }

}
