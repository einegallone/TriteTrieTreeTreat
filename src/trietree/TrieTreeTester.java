package trietree;

import java.util.Collections;
import java.util.HashMap;

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
        assertEquals("Empty tree has no entries", Collections.emptyMap(), tt_integer.allKeyValues());
    }

    @Test
    public void testPut()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        HashMap<String, Integer> hm_expected = new HashMap<String, Integer>();
        hm_expected.put("cat", 5);
        hm_expected.put("dog", -9);
        hm_expected.put("mouse", 0);

        assertFalse("Put null key returns false", tt_integer.put(null, 1));
        assertFalse("Put empty key returns false", tt_integer.put("", 1));
        assertTrue("Put new key returns true", tt_integer.put("cat", 5));
        assertFalse("Put existing key returns false", tt_integer.put("cat", 7));
        assertTrue("Put second unique key returns true", tt_integer.put("dog", -9));
        assertTrue("Put third unique key returns true", tt_integer.put("mouse", 0));
        assertEquals("Tree has 3 entries after putting 3 unique keys", 3, tt_integer.size());
        assertFalse("Tree is not empty after putting 3 unique keys", tt_integer.isEmpty());
        assertTrue("Tree has \"cat\", \"dog\", and \"mouse\" after put",
                   hm_expected.equals(tt_integer.allKeyValues()));
    }

    @Test
    public void testContains()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertFalse("Contains \"cat\" returns false before putting it", tt_integer.contains("cat"));

        tt_integer.put("cat", 5);
        assertFalse("Contains null key returns false", tt_integer.contains(null));
        assertFalse("Contains empty key returns false", tt_integer.contains(""));
        assertFalse("Contains non-existent key returns false", tt_integer.contains("catch"));
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
        assertEquals("Get non-existent key returns false", null, tt_integer.get("catch"));
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
        assertFalse("Update non-existent returns false", tt_integer.update("catch", 1));
        assertFalse("Update non-existent returns false", tt_integer.update("ca", 1));
        assertTrue("Update \"cat\" returns true after putting it", tt_integer.update("cat", 1));
        assertEquals("Get \"cat\" returns 1 after updating it", 1, (int) tt_integer.get("cat"));
    }

    @Test
    public void testRemove()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        HashMap<String, Integer> hm_expected = new HashMap<String, Integer>();
        hm_expected.put("dog", -9);
        hm_expected.put("mouse", 0);

        assertFalse("Remove \"cat\" returns false before putting it", tt_integer.remove("cat"));

        tt_integer.put("cat", 5);
        tt_integer.put("c", 5);
        tt_integer.put("dog", -9);
        tt_integer.put("dogged", -9);
        tt_integer.put("mouse", 0);
        assertFalse("Remove null key returns false", tt_integer.remove(null));
        assertFalse("Remove empty key returns false", tt_integer.remove(""));
        assertFalse("Remove non-existent key returns false", tt_integer.remove("catch"));
        assertFalse("Remove non-existent returns false", tt_integer.remove("ca"));
        assertTrue("Remove \"c\" returns true", tt_integer.remove("c"));
        assertTrue("\"cat\" should still be in the tree after removing \"c\"", tt_integer.contains("cat"));
        assertTrue("Remove \"cat\" returns true after putting it", tt_integer.remove("cat"));
        assertTrue("Remove \"dogged\" returns true", tt_integer.remove("dogged"));
        assertEquals("Tree has 2 entries left", 2, tt_integer.size());
        assertTrue("Tree has \"dog\" and \"mouse\" after left",
                   hm_expected.equals(tt_integer.allKeyValues()));
    }

    @Test
    public void testRemoveAll()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        HashMap<String, Integer> hm_expected = new HashMap<String, Integer>();

        tt_integer.put("cat", 5);
        tt_integer.put("dog", -9);
        tt_integer.put("mouse", 0);
        assertEquals("Tree has 3 entries after putting 3 unique keys", 3, tt_integer.size());

        tt_integer.removeAll();
        assertEquals("Empty tree has 0 entries", 0, tt_integer.size());
        assertTrue("Empty tree returns true", tt_integer.isEmpty());
        assertTrue("Empty tree has no entries", hm_expected.equals(tt_integer.allKeyValues()));
    }

    @Test
    public void testKeysWithPrefix()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();

        assertEquals("Empty tree has nothing with prefix \"\"", Collections.emptyMap(),
                     tt_integer.keyValueCollectionWithPrefix(""));
        assertEquals("Empty tree has nothing with prefix \"\"", Collections.emptyMap(),
                     tt_integer.keyValueCollectionWithPrefix(null));

        tt_integer.put("cat", 1);
        tt_integer.put("catastrophe", 2);
        tt_integer.put("cats", 3);
        tt_integer.put("catnap", 4);
        tt_integer.put("catacomb", 5);
        tt_integer.put("ca", 6);
        tt_integer.put("c", 7);
        tt_integer.put("dog", 8);
        tt_integer.put("dogged", 9);

        HashMap<String, Integer> hm_expected1 = new HashMap<String, Integer>();
        hm_expected1.put("catacomb", 5);
        hm_expected1.put("catastrophe", 2);
        assertTrue("Prefix 'cata' has \"catacomb\" and \"catastrophe\"",
                   hm_expected1.equals(tt_integer.keyValueCollectionWithPrefix("cata")));

        hm_expected1.put("cat", 1);
        hm_expected1.put("cats", 3);
        hm_expected1.put("catnap", 4);
        assertTrue("Prefix 'cat' has \"cat\", \"catastrophe\", \"cats\", \"catnap\", and \"catacomb\"",
                   hm_expected1.equals(tt_integer.keyValueCollectionWithPrefix("cat")));

        hm_expected1.put("ca", 6);
        hm_expected1.put("c", 7);
        assertTrue("Prefix 'c' has \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", and \"c\"",
                   hm_expected1.equals(tt_integer.keyValueCollectionWithPrefix("c")));

        HashMap<String, Integer> hm_expected2 = new HashMap<String, Integer>();
        hm_expected2.put("dog", 8);
        hm_expected2.put("dogged", 9);
        assertTrue("Prefix 'd' has \"dog\" and \"dogged\"",
                   hm_expected2.equals(tt_integer.keyValueCollectionWithPrefix("d")));

        assertEquals("Prefix 'z' has no matches", Collections.emptyMap(), tt_integer.keyValueCollectionWithPrefix("z"));

        hm_expected1.put("dog", 8);
        hm_expected1.put("dogged", 9);
        assertTrue("Prefix \"\" has \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", \"c\", \"dog\", and \"dogged\"",
                   hm_expected1.equals(tt_integer.keyValueCollectionWithPrefix("")));
    }

    @Test
    public void testAllKeys()
    {
        TrieTree<Integer> tt_integer = new TrieTree<Integer>();
        assertEquals("Empty tree has nothing", Collections.emptyMap(), tt_integer.allKeyValues());

        tt_integer.put("cat", 1);
        tt_integer.put("catastrophe", 2);
        tt_integer.put("cats", 3);
        tt_integer.put("catnap", 4);
        tt_integer.put("catacomb", 5);
        tt_integer.put("ca", 6);
        tt_integer.put("c", 7);
        tt_integer.put("dog", 8);
        tt_integer.put("dogged", 9);

        HashMap<String, Integer> hm_expected = new HashMap<String, Integer>();
        hm_expected.put("cat", 1);
        hm_expected.put("catastrophe", 2);
        hm_expected.put("cats", 3);
        hm_expected.put("catnap", 4);
        hm_expected.put("catacomb", 5);
        hm_expected.put("ca", 6);
        hm_expected.put("c", 7);
        hm_expected.put("dog", 8);
        hm_expected.put("dogged", 9);
        assertTrue("All keys: \"cat\", \"catastrophe\", \"cats\", \"catnap\", \"catacomb\", \"ca\", \"c\", \"dog\", and \"dogged\"",
                   hm_expected.equals(tt_integer.allKeyValues()));
    }
}