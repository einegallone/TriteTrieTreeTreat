package trietree;

import org.hamcrest.Matchers;
import org.junit.Test;
import static org.junit.Assert.*;

public class TrieTreeTester
{
    @Test
    public void testEmptyTree()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertEquals("Empty tree has 0 entries", 0, tt_integer.size());
        assertTrue("Empty tree returns true", tt_integer.isEmpty());
        assertThat("Empty tree has no entries", tt_integer.allKeys(), Matchers.emptyIterable());
    }

    @Test
    public void testPut()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertFalse("Put null key returns false", tt_integer.put(null, 1));
        assertFalse("Put empty key returns false", tt_integer.put("", 1));
        assertTrue("Put new key returns true", tt_integer.put("cat", 5));
        assertFalse("Put existing key returns false", tt_integer.put("cat", 7));
        assertTrue("Put second unique key returns true", tt_integer.put("dog", -9));
        assertTrue("Put third unique key returns true", tt_integer.put("mouse", 0));
        assertEquals("Tree has 3 entries after putting 3 unique keys", 3, tt_integer.size());
        assertFalse("Tree is not empty after putting 3 unique keys", tt_integer.isEmpty());
        assertThat("Tree has \"cat\", \"dog\", and \"mouse\" after put", tt_integer.allKeys(),
                   Matchers.containsInAnyOrder("cat", "dog", "mouse"));
    }

    @Test
    public void testContains()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertFalse("Contains \"cat\" returns false before putting it", tt_integer.contains("cat"));

        tt_integer.put("cat", 5);
        assertFalse("Contains null key returns false", tt_integer.contains(null));
        assertFalse("Contains empty key returns false", tt_integer.contains(""));
        assertTrue("Contains \"cat\" returns true after putting it", tt_integer.contains("cat"));
    }

    @Test
    public void testGet()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertEquals("Get \"cat\" returns false before putting it", null, tt_integer.get("cat"));

        tt_integer.put("cat", 5);
        assertEquals("Get null key returns false", null, tt_integer.get(null));
        assertEquals("Get empty key returns false", null, tt_integer.get(""));
        assertEquals("Get \"cat\" returns 5 after putting it", 5, (int) tt_integer.get("cat"));
    }

    @Test
    public void testUpdate()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertFalse("Update \"cat\" returns false before putting it", tt_integer.update("cat", 1));

        tt_integer.put("cat", 5);
        assertFalse("Update null key returns false", tt_integer.update(null, 1));
        assertFalse("Update empty key returns false", tt_integer.update("", 1));
        assertTrue("Update \"cat\" returns true after putting it", tt_integer.update("cat", 1));
    }

    @Test
    public void testRemove()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertFalse("Remove \"cat\" returns false before putting it", tt_integer.remove("cat"));

        tt_integer.put("cat", 5);
        tt_integer.put("dog", -9);
        tt_integer.put("mouse", 0);
        assertFalse("Remove null key returns false", tt_integer.remove(null));
        assertFalse("Remove empty key returns false", tt_integer.remove(""));
        assertTrue("Remove \"cat\" returns true after putting it", tt_integer.remove("cat"));
        assertEquals("Tree has 2 entries after removing \"cat\"", 2, tt_integer.size());
        assertThat("Tree has \"dog\" and \"mouse\" after removing \"cat\"", tt_integer.allKeys(),
                   Matchers.containsInAnyOrder("dog", "mouse"));
    }

    @Test
    public void testRemoveAll()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        tt_integer.put("cat", 5);
        tt_integer.put("dog", -9);
        tt_integer.put("mouse", 0);
        assertEquals("Tree has 3 entries after putting 3 unique keys", 3, tt_integer.size());

        tt_integer.removeAll();
        assertEquals("Empty tree has 0 entries", 0, tt_integer.size());
        assertTrue("Empty tree returns true", tt_integer.isEmpty());
        assertThat("Empty tree has no entries", tt_integer.allKeys(), Matchers.emptyIterable());
    }

    @Test
    public void testKeysWithPrefix()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        assertThat("Empty tree has nothing with prefix \"\"", tt_integer.keysWithPrefix(""),
                   Matchers.emptyIterable());

        tt_integer.put("cat", 1);
        tt_integer.put("catastrophe", 1);
        tt_integer.put("cats", 1);
        tt_integer.put("catnap", 1);
        tt_integer.put("catacomb", 1);
        tt_integer.put("ca", 1);
        tt_integer.put("c", 1);
        tt_integer.put("dog", 1);
        tt_integer.put("dogged", 1);
        assertThat("Prefix 'cata' has \"catacomb\" and \"catastrophe\"", tt_integer.keysWithPrefix("cata"),
                   Matchers.containsInAnyOrder("catacomb", "catastrophe"));
        assertThat("Prefix 'cat' has \"cat\", \"catastrophe\", \"cats\", \"catnap\", and \"catacomb\"",
                   tt_integer.keysWithPrefix("cat"),
                   Matchers.containsInAnyOrder("cat", "catastrophe", "cats", "catnap", "catacomb"));
        assertThat("Prefix 'c' has \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", and \"c\"",
                   tt_integer.keysWithPrefix("c"),
                   Matchers.containsInAnyOrder("cat", "catastrophe", "cats", "catnap", "catacomb", "ca", "c"));
        assertThat("Prefix 'd' has \"dog\" and \"dogged\"", tt_integer.keysWithPrefix("d"),
                   Matchers.containsInAnyOrder("dog", "dogged"));
        assertThat("Prefix 'z' has no matches", tt_integer.keysWithPrefix("z"), Matchers.emptyIterable());
        assertThat("Prefix \"\" has \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", \"c\", \"dog\", and \"dogged\"",
                   tt_integer.keysWithPrefix(""), Matchers.containsInAnyOrder("cat", "catastrophe", "cats",
                                                                              "catnap", "catacomb", "ca",
                                                                              "c", "dog", "dogged"));
    }

    @Test
    public void testAllKeys()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        assertThat("Empty tree has nothing", tt_integer.allKeys(), Matchers.emptyIterable());

        tt_integer.put("cat", 1);
        tt_integer.put("catastrophe", 1);
        tt_integer.put("cats", 1);
        tt_integer.put("catnap", 1);
        tt_integer.put("catacomb", 1);
        tt_integer.put("ca", 1);
        tt_integer.put("c", 1);
        tt_integer.put("dog", 1);
        tt_integer.put("dogged", 1);
        assertThat("All keys: \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", \"c\", \"dog\", and \"dogged\"",
                   tt_integer.allKeys(), Matchers.containsInAnyOrder("cat", "catastrophe", "cats",
                                                                              "catnap", "catacomb", "ca",
                                                                              "c", "dog", "dogged"));
    }
}