/*
 * Copyright (c) 2019-2022 Team Galacticraft
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.galacticraft.mod.mixin;

import dev.galacticraft.mod.Galacticraft;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.HeightLimitView;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public abstract class StructurePoolGeneratorMixin {
    @SuppressWarnings("UnnecessaryQualifiedMemberReference") // MCDev doesn't realize that it is required since it is targeting a private class
    @Inject(method = "Lnet/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator;generatePiece(Lnet/minecraft/structure/PoolStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IIZLnet/minecraft/world/HeightLimitView;)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", remap = false), require = 0)
    public void extraDebugInfoGC(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, int currentSize, boolean bl, HeightLimitView world, CallbackInfo ci) {
        if (Galacticraft.CONFIG_MANAGER.get().isDebugLogEnabled()) {
            Galacticraft.LOGGER.warn("Pool referencer: {}", piece.toString());
        }
    }
}
