package net.danygames2014.buildcraft.config;

import net.danygames2014.buildcraft.block.entity.pipe.transporter.ItemPipeTransporter.HandOffResult;
import net.glasslauncher.mods.gcapi3.api.ConfigEntry;

@SuppressWarnings("removal")
public class PipeConfig {
    @ConfigEntry(name = "Failed Insert Result", multiplayerSynced = true)
    public HandOffResult failedInsertResult = HandOffResult.DROP;

    @ConfigEntry(name = "Render inner pipe")
    public Boolean renderInnerPipe = true;
    
    @ConfigEntry(name = "Pipe Update Distance (Server-Side)", minLength = 16L, maxLength = 64L)
    public Integer pipeUpdateDistance = 24;
}
