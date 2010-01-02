/*
 *  Jajuk
 *  Copyright (C) 2003-2010 The Jajuk Team
 *  http://jajuk.info
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $Revision: 3132 $
 */
package org.jajuk.services.players;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jajuk.JUnitHelpers;
import org.jajuk.JajukTestCase;
import org.jajuk.base.File;
import org.jajuk.util.error.JajukException;

/**
 * 
 */
public class TestQueueImpl extends JajukTestCase {

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#add(org.jajuk.services.players.StackItem)}.
   * @throws Exception 
   */
  public void testAddStackItem() throws Exception {
    QueueImpl impl = new QueueImpl();
    impl.add(new StackItem(JUnitHelpers.getFile(1, false)));
    assertEquals(1, impl.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#add(int, org.jajuk.services.players.StackItem)}.
   * @throws Exception 
   */
  public void testAddIntStackItem() throws Exception {
    QueueImpl impl = new QueueImpl();
    impl.add(new StackItem(JUnitHelpers.getFile(1, false)));
    assertEquals(1, impl.size());
    impl.add(0, new StackItem(JUnitHelpers.getFile(2, false)));
    assertEquals(2, impl.size());

    // "2" should be first!
    assertEquals("2", impl.get(0).getFile().getID());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#addAll(java.util.Collection)}.
   * @throws Exception 
   */
  public void testAddAllCollectionOfQextendsStackItem() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#addAll(int, java.util.Collection)}.
   * @throws Exception 
   */
  public void testAddAllIntCollectionOfQextendsStackItem() throws Exception {
    QueueImpl impl = new QueueImpl();

    impl.add(new StackItem(JUnitHelpers.getFile(10, false)));
    assertEquals(1, impl.size());

    List<StackItem> set = new ArrayList<StackItem>();
    set.add(new StackItem(JUnitHelpers.getFile(1, false)));
    set.add(new StackItem(JUnitHelpers.getFile(2, false)));
    set.add(new StackItem(JUnitHelpers.getFile(3, false)));

    impl.addAll(0, set);
    assertEquals(4, impl.size());
    assertEquals("1", impl.get(0).getFile().getID());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#clear()}.
   * @throws Exception 
   */
  public void testClear() throws Exception {
    QueueImpl impl = new QueueImpl();

    impl.add(new StackItem(JUnitHelpers.getFile(10, false)));
    assertEquals(1, impl.size());

    impl.clear();
    assertEquals(0, impl.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#contains(java.lang.Object)}.
   * @throws Exception 
   */
  public void testContains() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertTrue(impl.contains(new StackItem(JUnitHelpers.getFile(1, false))));
    assertFalse(impl.contains(new StackItem(JUnitHelpers.getFile(4, false))));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#containsAll(java.util.Collection)}.
   * @throws Exception 
   */
  public void testContainsAll() throws Exception {
    QueueImpl impl = new QueueImpl();

    List<StackItem> set = prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertTrue(impl.containsAll(set));

    // add a file that is not in the queue
    set.add(new StackItem(JUnitHelpers.getFile(4, false)));
    assertFalse(impl.containsAll(set));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#get(int)}.
   */
  public void testGet() throws Exception {
    // tested above
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#indexOf(java.lang.Object)}.
   */
  public void testIndexOf() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertEquals(0, impl.indexOf(new StackItem(JUnitHelpers.getFile(1, false))));
    assertEquals(1, impl.indexOf(new StackItem(JUnitHelpers.getFile(2, false))));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#isEmpty()}.
   */
  public void testIsEmpty() throws Exception {
    QueueImpl impl = new QueueImpl();

    assertTrue(impl.isEmpty());

    impl.add(new StackItem(JUnitHelpers.getFile(1, false)));

    assertFalse(impl.isEmpty());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#iterator()}.
   */
  public void testIterator() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    Iterator<StackItem> it = impl.iterator();
    assertTrue(it.hasNext());
    it.next();
    assertTrue(it.hasNext());
    it.next();
    assertTrue(it.hasNext());
    it.next();
    assertFalse(it.hasNext());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#lastIndexOf(java.lang.Object)}.
   */
  public void testLastIndexOf() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertEquals(2, impl.lastIndexOf(new StackItem(JUnitHelpers.getFile(3, false))));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#listIterator()}.
   */
  public void testListIterator() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertNotNull(impl.listIterator());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#listIterator(int)}.
   */
  public void testListIteratorInt() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    assertNotNull(impl.listIterator(1));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#remove(java.lang.Object)}.
   */
  public void testRemoveObject() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);
    assertEquals(3, impl.size());

    // first it works
    assertTrue(impl.remove(new StackItem(JUnitHelpers.getFile(2, false))));

    // then the element is not found any more
    assertFalse(impl.remove(new StackItem(JUnitHelpers.getFile(2, false))));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#remove(int)}.
   */
  public void testRemoveInt() throws Exception {
    QueueImpl impl = new QueueImpl();

    prepareQueueImpl(impl);

    // first it removes an item
    assertNotNull(impl.remove(2));

    // then there is no more item to remove
    try {
      assertNull(impl.remove(2));
      fail("Should catch an Exception here");
    } catch (IndexOutOfBoundsException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Index: 2, Size: 2"));
    }
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#removeAll(java.util.Collection)}.
   */
  public void testRemoveAll() throws Exception {
    QueueImpl impl = new QueueImpl();

    List<StackItem> set = prepareQueueImpl(impl);

    assertTrue(impl.removeAll(set));
    assertEquals(0, impl.size());

    // no more changes
    assertFalse(impl.removeAll(set));
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#retainAll(java.util.Collection)}.
   */
  public void testRetainAll() throws Exception {
    QueueImpl impl = new QueueImpl();

    List<StackItem> set = prepareQueueImpl(impl);

    impl.retainAll(set);
    assertEquals(3, impl.size());

    impl.retainAll(new HashSet<StackItem>());
    assertEquals(0, impl.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#set(int, org.jajuk.services.players.StackItem)}.
   */
  public void testSet() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    impl.set(0, new StackItem(JUnitHelpers.getFile(4, false)));

    assertEquals("4", impl.get(0).getFile().getID());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#size()}.
   */
  public void testSize() throws Exception {
    // tested above
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#subList(int, int)}.
   */
  public void testSubList() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    List<StackItem> items = impl.subList(1, 3);
    assertEquals(2, items.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#toArray()}.
   */
  public void testToArray() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    Object[] arr = impl.toArray();
    assertEquals(3, arr.length);
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#toArray(T[])}.
   */
  public void testToArrayTArray() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    StackItem[] arr = impl.toArray(new StackItem[] {});
    assertEquals(3, arr.length);
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#getQueue()}.
   */
  public void testGetQueue() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    List<StackItem> queue = impl.getQueue();
    assertEquals(3, queue.size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#sizePlanned()}.
   */
  public void testSizePlanned() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    assertEquals(0, impl.sizePlanned());
    impl.addPlanned(new StackItem(JUnitHelpers.getFile(2, false)));

    assertEquals(1, impl.sizePlanned());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#addPlanned(org.jajuk.services.players.StackItem)}.
   */
  public void testAddPlannedStackItem() throws Exception {
    // tested above
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#addPlanned(java.util.List)}.
   */
  public void testAddPlannedListOfStackItem() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    assertEquals(2, impl.sizePlanned());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#removePlanned(int)}.
   */
  public void testRemovePlanned() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    assertNotNull(impl.removePlanned(0));
    assertNotNull(impl.removePlanned(0));
    try {
      assertNull(impl.removePlanned(0));
    } catch (IndexOutOfBoundsException e) {
      assertTrue(e.getMessage(), e.getMessage().contains("Index: 0, Size: 0"));
    }
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#getPlanned(int)}.
   */
  public void testGetPlannedInt() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    assertEquals("9", impl.getPlanned(0).getFile().getID());
    assertEquals("8", impl.getPlanned(1).getFile().getID());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#getPlanned()}.
   */
  public void testGetPlanned() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    assertEquals(2, impl.getPlanned().size());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#removePlannedFromList(java.util.List)}.
   */
  public void testRemovePlannedFromList() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    List<File> items = new ArrayList<File>();

    impl.removePlannedFromList(items);

    assertEquals(0, items.size());

    items.add(JUnitHelpers.getFile(2, false));
    items.add(JUnitHelpers.getFile(8, false)); // this should be removed
    items.add(JUnitHelpers.getFile(11, false));

    assertEquals(3, items.size());

    impl.removePlannedFromList(items);

    assertEquals(2, items.size());
    assertEquals("2", items.get(0).getID());
    assertEquals("11", items.get(1).getID());

  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#clearPlanned()}.
   */
  public void testClearPlanned() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    impl.clearPlanned();

    assertEquals(3, impl.size());
    assertEquals(0, impl.sizePlanned());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#containsRepeat()}.
   */
  public void testContainsRepeat() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    // repeat is not automatically set
    assertFalse(impl.containsRepeat());

    impl.get(1).setRepeat(true);

    assertTrue(impl.containsRepeat());

    impl.get(1).setRepeat(false);

    assertFalse(impl.containsRepeat());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#popNextPlanned()}.
   */
  public void testPopNextPlanned() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    prepareQueueImplPlanned(impl);

    assertNotNull(impl.popNextPlanned());
    assertNotNull(impl.popNextPlanned());
    assertNull(impl.popNextPlanned());
  }

  /**
   * Test method for {@link org.jajuk.services.players.QueueImpl#containsOnlyRepeat()}.
   */
  public void testContainsOnlyRepeat() throws Exception {
    QueueImpl impl = new QueueImpl();
    prepareQueueImpl(impl);

    // repeat is not automatically set
    assertFalse(impl.containsOnlyRepeat());

    impl.get(1).setRepeat(true);

    assertFalse(impl.containsOnlyRepeat());

    impl.get(0).setRepeat(true);
    impl.get(2).setRepeat(true);

    assertTrue(impl.containsOnlyRepeat());

    impl.get(1).setRepeat(false);

    assertFalse(impl.containsOnlyRepeat());
  }

  private List<StackItem> prepareQueueImpl(QueueImpl impl) throws JajukException, Exception {
    List<StackItem> set = new ArrayList<StackItem>();
    set.add(new StackItem(JUnitHelpers.getFile(1, false)));
    set.add(new StackItem(JUnitHelpers.getFile(2, false)));
    set.add(new StackItem(JUnitHelpers.getFile(3, false)));

    impl.addAll(set);
    return set;
  }

  private List<StackItem> prepareQueueImplPlanned(QueueImpl impl) throws JajukException, Exception {
    List<StackItem> set = new ArrayList<StackItem>();
    set.add(new StackItem(JUnitHelpers.getFile(9, false)));
    set.add(new StackItem(JUnitHelpers.getFile(8, false)));

    impl.addPlanned(set);
    return set;
  }
}
