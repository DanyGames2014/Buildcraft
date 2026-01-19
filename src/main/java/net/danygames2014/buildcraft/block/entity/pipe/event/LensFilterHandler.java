package net.danygames2014.buildcraft.block.entity.pipe.event;

import net.danygames2014.buildcraft.block.entity.pipe.PipeBlockEntity;
import net.danygames2014.buildcraft.block.entity.pipe.PipePluggable;
import net.danygames2014.buildcraft.pluggable.LensPluggable;
import net.modificationstation.stationapi.api.util.math.Direction;

import java.util.LinkedList;

public class LensFilterHandler {
    @PipeEventPriority(priority = -100)
    public void eventHandler(ItemPipeEvent.FindDest event){
        LinkedList<Direction> correctColored = new LinkedList<>();
        LinkedList<Direction> notColored = new LinkedList<>();
        boolean encounteredColor = false;
        int myColor = event.item.getColor();

        for(Direction direction : event.destinations){
            int sideColor = -1;
            int sideLensColor = -1;

            // Get the side's color
            // (1/2) From this pipe's outpost
            PipePluggable pluggable = event.pipe.getPipePluggable(direction);
            if (pluggable instanceof LensPluggable) {
                if (((LensPluggable) pluggable).isFilter) {
                    sideColor = ((LensPluggable) pluggable).color;
                } else {
                    sideLensColor = ((LensPluggable) pluggable).color;
                }
            }

            // (2/2) From the other pipe's outpost
            PipeBlockEntity otherPipe = event.pipe.getNeighborPipe(direction);
            if (otherPipe != null) {
                pluggable = otherPipe.getPipePluggable(direction.getOpposite());
                if (pluggable instanceof LensPluggable && ((LensPluggable) pluggable).isFilter) {
                    int otherColor = ((LensPluggable) pluggable).color;
                    if (sideColor >= 0 && otherColor != sideColor) {
                        // Filter colors conflict - the side is unpassable
                        continue;
                    } else if (sideLensColor != -1 && sideColor != otherColor) {
                        // The closer lens color differs from the further away filter color - the side is unpassable
                        continue;
                    } else {
                        sideColor = otherColor;
                    }
                }
            }

            if (myColor == sideColor) {
                encounteredColor = true;
                correctColored.add(direction);
            }

            if (sideColor == -1) {
                notColored.add(direction);
            }
        }

        event.destinations.clear();
        event.destinations.addAll(encounteredColor ? correctColored : notColored);
    }
}
