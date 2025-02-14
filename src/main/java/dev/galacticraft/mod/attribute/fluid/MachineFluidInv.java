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

package dev.galacticraft.mod.attribute.fluid;

import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.filter.ConstantFluidFilter;
import alexiil.mc.lib.attributes.fluid.filter.FluidFilter;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.volume.FluidKey;
import dev.galacticraft.mod.Constant;
import dev.galacticraft.mod.api.screen.MachineScreenHandler;
import dev.galacticraft.mod.attribute.Automatable;
import dev.galacticraft.mod.screen.slot.SlotType;
import dev.galacticraft.mod.screen.tank.OxygenTank;
import dev.galacticraft.mod.screen.tank.Tank;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/TeamGalacticraft">TeamGalacticraft</a>
 */
public class MachineFluidInv extends SimpleFixedFluidInv implements Automatable {
    private final SlotType[] slotTypes;
    private final FluidFilter[] filters;
    private final TankProvider[] tankProviders;

    public MachineFluidInv(FluidAmount capacity, SlotType[] slotTypes, FluidFilter[] filters, TankProvider[] tankProviders) {
        super(filters.length, capacity);
        this.slotTypes = slotTypes;
        this.filters = filters;
        this.tankProviders = tankProviders;
    }

    @Override
    public FluidFilter getFilterForTank(int slot) {
        if (slot < 0 || slot >= this.getTankCount()) return ConstantFluidFilter.NOTHING;
        return this.filters[slot];
    }

    @Override
    public boolean isFluidValidForTank(int tank, FluidKey fluid) {
        return fluid.isEmpty() || this.getFilterForTank(tank).matches(fluid);
    }

    public void createTanks(MachineScreenHandler<?> screenHandler) {
        for (int i = 0; i < getTankCount(); i++) {
            screenHandler.addTank(this.tankProviders[i].createTank(i, screenHandler.machine.fluidInv()));
        }
    }

    @Override
    public SlotType[] getTypes() {
        return this.slotTypes;
    }

    public static class Builder {
        private final FluidFilter EMPTY_ONLY = FluidKey::isEmpty;
        private final FluidAmount capacity;
        private final List<SlotType> slotTypes = new ArrayList<>();
        private final List<FluidFilter> filters = new ArrayList<>();
        private final List<TankProvider> tankProviders = new ArrayList<>();

        private Builder(FluidAmount capacity) {
            this.capacity = capacity;
        }

        public static Builder create(FluidAmount capacity) {
            return new Builder(capacity);
        }

        public void addTank(int index, SlotType type, FluidFilter filter, int x, int y, int s) {
            this.tankProviders.add(index, new DefaultTankProvider(x, y, s));
            this.slotTypes.add(index, type);
            this.filters.add(index, filter.or(EMPTY_ONLY));
        }

        public void addTank(int index, SlotType type, int x, int y, int s) {
            this.tankProviders.add(index, new DefaultTankProvider(x, y, s));
            this.slotTypes.add(index, type);
            this.filters.add(index, ConstantFluidFilter.ANYTHING);
        }

        public void addTank(int index, SlotType type, FluidFilter filter, int x, int y) {
            this.tankProviders.add(index, new DefaultTankProvider(x, y, 1));
            this.slotTypes.add(index, type);
            this.filters.add(index, filter.or(EMPTY_ONLY));
        }

        public void addTank(int index, SlotType type, int x, int y) {
            this.tankProviders.add(index, new DefaultTankProvider(x, y, 1));
            this.slotTypes.add(index, type);
            this.filters.add(index, ConstantFluidFilter.ANYTHING);
        }

        public void addTank(int index, SlotType type, FluidFilter filter, TankProvider tankProvider) {
            this.tankProviders.add(index, tankProvider);
            this.slotTypes.add(index, type);
            this.filters.add(index, filter.or(EMPTY_ONLY));
        }

        public void addTank(int index, SlotType type, TankProvider tankProvider) {
            this.tankProviders.add(index, tankProvider);
            this.slotTypes.add(index, type);
            this.filters.add(index, ConstantFluidFilter.ANYTHING);
        }

        public MachineFluidInv build() {
            return new MachineFluidInv(this.capacity, this.slotTypes.toArray(new SlotType[0]), this.filters.toArray(new FluidFilter[0]), this.tankProviders.toArray(new TankProvider[0]));
        }

        public void addLOXTank(int index, SlotType type, int x, int y) {
            this.addTank(index, type, Constant.Filter.LOX_ONLY, new OxygenTankProvider(x, y));
        }
    }

    public interface TankProvider {
        Tank createTank(int index, FixedFluidInv inv);
    }

    public record DefaultTankProvider(int x, int y, int scale) implements TankProvider {
        @Override
        public Tank createTank(int index, FixedFluidInv inv) {
            return new Tank(index, inv, this.x, this.y, this.scale);
        }
    }

    public record OxygenTankProvider(int x, int y) implements TankProvider {
        @Override
        public Tank createTank(int index, FixedFluidInv inv) {
            return new OxygenTank(index, inv, x, y);
        }
    }
}