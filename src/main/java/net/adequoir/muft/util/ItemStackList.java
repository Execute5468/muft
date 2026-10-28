package net.adequoir.muft.util;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Unmodifiable
public final class ItemStackList extends AbstractList<ItemStack> {
    private static final ItemStackList EMPTY = new ItemStackList(new ItemStack[0]);

    private final ItemStack[] stacks;

    private ItemStackList(ItemStack[] stacks) { this.stacks = stacks; }

    public static ItemStackList of() { return EMPTY; }
    public static ItemStackList of(ItemStack... stacks) { return stacks.length == 0 ? EMPTY : new ItemStackList(stacks); }

    public @Override int size() { return stacks.length; }
    public @Override boolean isEmpty() { return stacks.length == 0; }

    public @Override ItemStack get(int index) { return stacks[index]; }
    public @Override ItemStack set(int index, ItemStack element) { throw new UnsupportedOperationException(); }

    public @Override boolean add(ItemStack stack) { throw new UnsupportedOperationException(); }
    public @Override void add(int index, ItemStack element) { throw new UnsupportedOperationException(); }
    public @Override void addFirst(ItemStack stack) { throw new UnsupportedOperationException(); }
    public @Override void addLast(ItemStack stack) { throw new UnsupportedOperationException(); }

    public @Override boolean addAll(Collection<? extends ItemStack> c) { throw new UnsupportedOperationException(); }
    public @Override boolean addAll(int index, Collection<? extends ItemStack> c) { throw new UnsupportedOperationException(); }

    public @Override ItemStack remove(int index) { throw new UnsupportedOperationException(); }
    public @Override boolean remove(Object o) { throw new UnsupportedOperationException(); }

    public @Override ItemStack removeFirst() { throw new UnsupportedOperationException(); }
    public @Override ItemStack removeLast() { throw new UnsupportedOperationException(); }

    public @Override boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    public @Override boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    public @Override boolean removeIf(Predicate<? super ItemStack> filter) { throw new UnsupportedOperationException(); }

    public @Override void replaceAll(UnaryOperator<ItemStack> operator) { throw new UnsupportedOperationException(); }
    public @Override void sort(@Nullable Comparator<? super ItemStack> c) { throw new UnsupportedOperationException(); }

    public @Override void clear() { throw new UnsupportedOperationException(); }
    protected @Override void removeRange(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }

    private static class Itr implements ListIterator<ItemStack> {
        private final ItemStack[] stacks;
        private int cursor;
        Itr(ItemStack[] stacks, int startIndex) {
            this.stacks = stacks;
            this.cursor = startIndex;
        }

        public @Override boolean hasNext() { return cursor < stacks.length; }
        public @Override int nextIndex() { return cursor; }
        public @Override ItemStack next() {
            if (!hasNext()) throw new NoSuchElementException();
            return stacks[cursor++];
        }

        public @Override boolean hasPrevious() { return cursor > 0; }
        public @Override int previousIndex() { return cursor - 1; }
        public @Override ItemStack previous() {
            if (!hasPrevious()) throw new NoSuchElementException();
            return stacks[--cursor];
        }

        public @Override void remove() { throw new UnsupportedOperationException(); }
        public @Override void set(ItemStack stack) { throw new UnsupportedOperationException(); }
        public @Override void add(ItemStack stack) { throw new UnsupportedOperationException(); }
    }
}
