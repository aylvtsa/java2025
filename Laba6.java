import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;

/**
 * ЛР6: Робота з колекціями в мові Java.
 *
 * Клас {@code ApplianceList} реалізує інтерфейс {@link java.util.List} для
 * зберігання об'єктів типу {@link Appliance} у вигляді
 * однозв’язного списку (singly linked list).
 *
 * Тип елементів колекції – поліморфний клас з ЛР5 {@link Appliance}.
 */
class ApplianceList implements List<Appliance> {

    /** Внутрішній елемент однозв’язного списку. */
    private static final class Node {
        Appliance item;
        Node next;

        Node(Appliance item, Node next) {
            this.item = item;
            this.next = next;
        }
    }

    /** Голова списку (перший елемент) або {@code null}, якщо список порожній. */
    private Node head;

    /** Поточна кількість елементів у списку. */
    private int size;

    // -------------------------------------------------------------------------
    // Конструктори
    // -------------------------------------------------------------------------

    /** Порожній конструктор: створює пустий список. */
    public ApplianceList() {
        // head = null; size = 0;
    }

    /**
     * Конструктор від одного приладу.
     *
     * @param appliance прилад, який буде першим елементом
     * @throws NullPointerException якщо {@code appliance == null}
     */
    public ApplianceList(Appliance appliance) {
        add(Objects.requireNonNull(appliance, "Прилад не може бути null"));
    }

    /**
     * Конструктор від стандартної колекції приладів.
     *
     * @param source колекція-джерело
     * @throws NullPointerException якщо {@code source == null}
     */
    public ApplianceList(Collection<? extends Appliance> source) {
        Objects.requireNonNull(source, "Колекція-джерело не може бути null");
        addAll(source);
    }

    // -------------------------------------------------------------------------
    // Допоміжні методи
    // -------------------------------------------------------------------------

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }

    private Node nodeAt(int index) {
        Node x = head;
        for (int i = 0; i < index; i++) {
            x = x.next;
        }
        return x;
    }

    private void linkLast(Appliance a) {
        Node newNode = new Node(a, null);
        if (head == null) {
            head = newNode;
        } else {
            Node tail = head;
            while (tail.next != null) {
                tail = tail.next;
            }
            tail.next = newNode;
        }
        size++;
    }

    private void linkAt(int index, Appliance a) {
        checkPositionIndex(index);
        if (index == 0) {
            head = new Node(a, head);
        } else {
            Node prev = nodeAt(index - 1);
            prev.next = new Node(a, prev.next);
        }
        size++;
    }

    private Appliance unlink(Node prev, Node target) {
        Appliance item = target.item;
        if (prev == null) {
            head = target.next;
        } else {
            prev.next = target.next;
        }
        size--;
        return item;
    }

    // -------------------------------------------------------------------------
    // Реалізація List
    // -------------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public Iterator<Appliance> iterator() {
        return new Itr();
    }

    /** Простіший ітератор: тільки вперед, без remove. */
    private final class Itr implements Iterator<Appliance> {
        Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Appliance next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            Appliance item = current.item;
            current = current.next;
            return item;
        }
    }

    @Override
    public Object[] toArray() {
        Object[] arr = new Object[size];
        int i = 0;
        for (Node x = head; x != null; x = x.next) {
            arr[i++] = x.item;
        }
        return arr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(
                a.getClass().getComponentType(), size);
        }
        int i = 0;
        for (Node x = head; x != null; x = x.next) {
            a[i++] = (T) x.item;
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(Appliance appliance) {
        linkLast(appliance);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        Node prev = null;
        Node cur = head;
        while (cur != null) {
            if (Objects.equals(cur.item, o)) {
                unlink(prev, cur);
                return true;
            }
            prev = cur;
            cur = cur.next;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Appliance> c) {
        boolean modified = false;
        for (Appliance a : c) {
            add(a);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Appliance> c) {
        checkPositionIndex(index);
        int i = index;
        for (Appliance a : c) {
            linkAt(i++, a);
        }
        return !c.isEmpty();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Node prev = null;
        Node cur = head;
        while (cur != null) {
            if (c.contains(cur.item)) {
                unlink(prev, cur);
                modified = true;
                cur = (prev == null) ? head : prev.next;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Node prev = null;
        Node cur = head;
        while (cur != null) {
            if (!c.contains(cur.item)) {
                unlink(prev, cur);
                modified = true;
                cur = (prev == null) ? head : prev.next;
            } else {
                prev = cur;
                cur = cur.next;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Appliance get(int index) {
        checkElementIndex(index);
        return nodeAt(index).item;
    }

    @Override
    public Appliance set(int index, Appliance element) {
        checkElementIndex(index);
        Node x = nodeAt(index);
        Appliance old = x.item;
        x.item = element;
        return old;
    }

    @Override
    public void add(int index, Appliance element) {
        linkAt(index, element);
    }

    @Override
    public Appliance remove(int index) {
        checkElementIndex(index);
        Node prev = (index == 0) ? null : nodeAt(index - 1);
        Node target = (prev == null) ? head : prev.next;
        return unlink(prev, target);
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;
        for (Node x = head; x != null; x = x.next) {
            if (Objects.equals(x.item, o)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int i = 0;
        int res = -1;
        for (Node x = head; x != null; x = x.next) {
            if (Objects.equals(x.item, o)) {
                res = i;
            }
            i++;
        }
        return res;
    }

    // Ці методи можна спростити: для лабораторної достатньо заглушок.
    @Override
    public ListIterator<Appliance> listIterator() {
        throw new UnsupportedOperationException("listIterator() не реалізовано");
    }

    @Override
    public ListIterator<Appliance> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator(int) не реалізовано ");
    }

    @Override
    public List<Appliance> subList(int fromIndex, int toIndex) {
        // Перевірка діапазону [fromIndex; toIndex)
        if (fromIndex < 0 || toIndex > size || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    "fromIndex=" + fromIndex + ", toIndex=" + toIndex + ", size=" + size);
        }
    
        ApplianceList result = new ApplianceList();
    
        // Проходимо список і копіюємо елементи з потрібного діапазону
        int i = 0;
        for (Node x = head; x != null && i < toIndex; x = x.next, i++) {
            if (i >= fromIndex) {
                result.add(x.item);
            }
        }
    
        return result;
    }
    

    @Override
    public Spliterator<Appliance> spliterator() {
        throw new UnsupportedOperationException("spliterator() не реалізовано ");
    }
}

/**
 * Головний клас ЛР6.
 *
 * У виконавчому методі демонструється використання {@link ApplianceList}
 * разом із ієрархією приладів з ЛР5.
 */
public class Laba6 {

    /**
     * Точка входу: створення власної колекції {@link ApplianceList},
     * наповнення її приладами та базові операції.
     *
     * @param args не використовується
     */

    /** Розділювач для виводу. */
    private static final String SEP = "-*".repeat(30);
    public static void main(String[] args) {
        try {
            // 1. Створення порожньої колекції
            ApplianceList list = new ApplianceList();
    
            // 2. Додаємо кілька приладів 
            list.add(new Kettle("Tefal", 2000, new EmissionRange(0, 5)));
            list.add(new Microwave("Samsung", 1500, new EmissionRange(2400, 2500)));
            list.add(new TV("LG", 180, new EmissionRange(470, 860)));
            list.add(new Kettle("Tefal", 2000, new EmissionRange(0, 5)));
            list.add(new Microwave("Samsung", 1500, new EmissionRange(2400, 2500)));
            list.add(new TV("LG", 180, new EmissionRange(470, 860)));
            list.add(new Fridge("Bosch", 300, new EmissionRange(0, 10)));
            list.add(new Laptop("HP", 90, new EmissionRange(2400, 5800)));

            System.out.println(SEP);
            System.out.println("Початковий список приладів:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(" [" + i + "] " + list.get(i));
            }
    
            // 3. Конструктор від одного елемента
            Appliance kettle = new Kettle("Philips", 2200, new EmissionRange(0, 5));
            ApplianceList one = new ApplianceList(kettle);
            System.out.println(SEP);
            System.out.println("\nКолекція, створена від одного приладу:");
            for (Appliance a : one) {
                System.out.println(" - " + a);
            }
    
            // 4. Конструктор від стандартної колекції (subList з java.util.List)
            List<Appliance> sub = list.subList(1, 4);
            ApplianceList copy = new ApplianceList(sub);
            System.out.println(SEP);
            System.out.println("\nКолекція, створена зі стандартного subList(1,4) іншої колекції:");
            for (int i = 0; i < copy.size(); i++) {
                int originalIndex = 1 + i; 
                System.out.println(" [" + originalIndex + "] " + copy.get(i));
            }
    
            // 5. Приклад операцій зі списком
            System.out.println(SEP);
            System.out.println("\nЕлемент з індексом 2 у початковому списку:");
            System.out.println(" * " + list.get(2));
    
            // Заміна елемента 
            System.out.println("\nЗамінюємо елемент з індексом 1:");
            Appliance old = list.get(1);
            Appliance newTV = new TV("Sony", 200, new EmissionRange(470, 860));
            System.out.println("   Було: " + old);
            System.out.println("   Стає: " + newTV);
            list.set(1, newTV);
    
            System.out.println("\nСписок після заміни:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(" [" + i + "] " + list.get(i));
            }
    
            // Пошук індексу холодильника 
            System.out.println(SEP);
            int fridgeIndex = list.indexOf(new Fridge("Bosch", 300, new EmissionRange(0, 10)));
            System.out.println("\nІндекс холодильника Bosch: " + fridgeIndex);
    
            // Видалення елемента 
            System.out.println(SEP);
            System.out.println("\nВидаляємо елемент з індексом 0:");
            Appliance toRemove = list.get(0);
            System.out.println("   Це елемент: " + toRemove);
            list.remove(0);
    
            System.out.println("\nСписок після видалення першого елемента:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(" [" + i + "] " + list.get(i));
            }
    
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Помилка індексу: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Помилка аргументів: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непередбачена помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}