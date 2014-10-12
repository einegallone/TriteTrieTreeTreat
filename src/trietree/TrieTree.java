package trietree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * This generic data type is a prefix trie tree that utilizes hashmaps to link
 * its branches. Each key is represented by a String and is associated with a
 * Value. Each character of the key String is a node and only the nodes that
 * represent the ends of keys have Values associated with them.
 */
public class TrieTree<Value>
{
    /* PRIVATE MEMBERS */
    private long sizeOfTrie; // Number of words added
    private Node rootNode; // Root node

    // Used to maintain link between characters of the keys.
    private class Node
    {
        private boolean wordEnd; // True signifies the end of a word.
        private Value value; // Value assigned when wordEnd is true.
        private long referenceCount; // Number of references on this node (i.e.
                                     // how many words in the tree have this
                                     // prefix). If we hit 0, this node should
                                     // be removed. Cannot go below 0.
        HashMap<Character, Node> childrenNodes; // Link branches via maps.
                                                // Alternatively, we could use a
                                                // 256-element array for the
                                                // extended ASCII character map.

        public Node()
        {
            childrenNodes = new HashMap<Character, Node>();
            wordEnd = false;
            referenceCount = 0;
            value = null;
        }

        public void setWordEndTrue(Value val)
        {
            wordEnd = true;
            value = val;
        }

        public void setWordEndFalse()
        {
            wordEnd = false;
            value = null;
        }

        public boolean isWordEnd()
        {
            return wordEnd;
        }

        public Value getValue()
        {
            return value;
        }

        public void incrementRefCount()
        {
            ++referenceCount;
        }

        public void decrementRefCount()
        {
            if (referenceCount > 0)
                --referenceCount;
        }

        public long getRefCount()
        {
            return referenceCount;
        }
    }

    // Pair <Node, character>
    private final class PairNodeChar
    {
        private Node node;
        private char child;

        PairNodeChar(Node n, char c)
        {
            node = n;
            child = c;
        }

        public Node getNode()
        {
            return node;
        }

        public char getChar()
        {
            return child;
        }
    }

    /* METHODS */
    public TrieTree()
    {
        sizeOfTrie = 0;
        rootNode = new Node();
    }

    /**
     * This method returns the number of keys in the tree.
     * 
     * @return Number of keys in the tree.
     */
    public long size()
    {
        return sizeOfTrie;
    }

    /**
     * This method reports whether the tree is empty.
     * 
     * @return True if the true is empty. False otherwise.
     */
    public boolean isEmpty()
    {
        return sizeOfTrie == 0;
    }

    /**
     * This method gets the Value associated with the key, if the key exists in
     * the tree.
     * 
     * @param key
     *            String of the key.
     * @return Value associated with the key. Null if the key does not exist or
     *         no Value associated with key.
     */
    public Value get(String key)
    {
        if (key == null || key.isEmpty() || isEmpty())
            return null;

        Node n = get(key, rootNode, false);
        return n == null ? null : n.getValue();
    }

    /**
     * This method is called recursively, going through one character at a time.
     * We traverse the trie tree until we reach the end of a branch or the key
     * end. At that point we assert whether the key end is right at the end of a
     * word in the tree or the isPrefix flag is true, meaning we're looking for
     * a prefix in the tree; if either is true, then we return the associated
     * Node. Otherwise we return null.
     * 
     * @return Node associated with the key. Null if the key does not exist.
     */
    private Node get(String key, Node parentNode, boolean isPrefix)
    {
        assert !key.isEmpty(); // Empty key should not be possible due to the
                               // check at the public call and the return
                               // when the character count hits 1.

        char currentCharacter = key.charAt(0);
        Node child = parentNode.childrenNodes.get(currentCharacter);
        if (child == null)
        {
            // We reached the end of the tree branch before the end of the key,
            // which means the key isn't in the tree.
            return null;
        }

        // If we're down to our last character, check if we're also at the last
        // character of a word. If we are, return the Node associated with the
        // key. Otherwise, return null because the key isn't in the
        // tree.
        if (key.length() == 1)
        {
            if (child.isWordEnd() || isPrefix)
                return child;

            return null;
        }

        // If we're not down to our last character, pass in the rest of the
        // key substring for another cycle.
        return get(key.substring(1), child, isPrefix);
    }

    /**
     * This method checks if a key is in the trie tree.
     * 
     * @param key
     *            String of the key.
     * @return True if the key is in the trie tree. False if it is not.
     */
    public boolean contains(String key)
    {
        if (key == null || key.isEmpty() || isEmpty())
            return false;

        return get(key, rootNode, false) != null;
    }

    /**
     * This method puts a string into the trie and associates a Value with the
     * string. Duplicates will not be added.
     * 
     * @param key
     *            String of the key.
     * @param val
     *            Value to associate with string.
     * @return True if put was successful. False if the key is empty or trie
     *         already contains key.
     */
    public boolean put(String key, Value val)
    {
        if (key == null || key.isEmpty() || contains(key))
            return false;

        return put(key, val, rootNode);
    }

    /**
     * This method is called recursively, going through one character at a time
     * and creating a node for each one. We continue to do this until the end of
     * the original key, where we also return true and propagate it to the
     * original caller.
     * 
     * @return True once we're finished adding the key. We can't really fail
     *         here.
     */
    private boolean put(String key, Value val, Node parentNode)
    {
        assert !key.isEmpty(); // Empty key should not be possible due to the
                               // check at the public call and the return
                               // when the character count hits 1.

        char currentCharacter = key.charAt(0);
        Node child = parentNode.childrenNodes.get(currentCharacter);
        if (child == null)
        {
            child = new Node();
            parentNode.childrenNodes.put(currentCharacter, child);
        }
        child.incrementRefCount();

        // If we're down to our last character, this is the end of the key so
        // return true for great success.
        if (key.length() == 1)
        {
            child.setWordEndTrue(val);
            ++sizeOfTrie;
            return true;
        }

        // If we're not down to our last character, pass in the rest of the
        // key substring for another cycle.
        return put(key.substring(1), val, child);
    }

    /**
     * This method updates the Value associated with the key.
     * 
     * @param key
     *            String of the key.
     * @param val
     *            Value to associate with string.
     * @return True if update was successful. False if the key is empty or trie
     *         does not contain key.
     */
    public boolean update(String key, Value val)
    {
        if (key == null || key.isEmpty() || isEmpty())
            return false;

        return update(key, val, rootNode);
    }

    /**
     * This method is called recursively, going through one character at a time
     * until the end of the key is reached during tree traversal. At the end, we
     * update the value if the key exists.
     * 
     * @return True once we're finished updating the value. False if the key
     *         doesn't exist.
     */
    private boolean update(String key, Value val, Node parentNode)
    {
        assert !key.isEmpty(); // Empty key should not be possible due to the
                               // check at the public call and the return
                               // when the character count hits 1.

        char currentCharacter = key.charAt(0);
        Node child = parentNode.childrenNodes.get(currentCharacter);
        if (child == null)
        {
            // We reached the end of the tree branch before the end of the key,
            // which means the key isn't in the tree.
            return false;
        }

        // If we're down to our last character, check if we're also at the last
        // character of a word. If we are, update the value of the key.
        // Otherwise, return false because the key isn't in the tree.
        if (key.length() == 1)
        {
            if (child.isWordEnd())
            {
                child.setWordEndTrue(val);
                return true;
            }

            return false;
        }

        // Pass in the rest of the key substring for another cycle.
        return update(key.substring(1), val, child);
    }

    /**
     * This method removes a key from the tree, if it exists.
     * 
     * @param key
     *            String of the key.
     * @return True if the key is removed. False if the key is not in the tree.
     */
    public boolean remove(String key)
    {
        if (key == null || key.isEmpty() || isEmpty())
            return false;

        // We will add <parentNode, char> pairs that are to be removed from
        // the tree.
        Stack<PairNodeChar> keyCharacters = new Stack<PairNodeChar>();

        return remove(key, rootNode, keyCharacters);
    }

    /**
     * This method is called recursively, going through one character at a time
     * to search for the key. With each recursion, we keep track of the
     * chracters in the key; in the end we delete the nodes if applicable. We
     * continue to do this until the end of the original key, where we then
     * determine whether the key is actually in the trie. If it is, then we
     * remove the key utilizing a data structure that maintains the characters
     * of the key. Otherwise, we return false.
     * 
     * @return True if remove was successful. False if the key is empty or trie
     *         did not contain key.
     */
    private boolean remove(String key, Node parentNode, Stack<PairNodeChar> keyCharacters)
    {
        assert !key.isEmpty(); // Empty key should not be possible due to the
                               // check at the public call and the return
                               // when the character count hits 1.

        char currentCharacter = key.charAt(0);
        Node child = parentNode.childrenNodes.get(currentCharacter);
        if (child == null)
        {
            // We reached the end of the tree branch before the end of the key,
            // which means the key isn't in the tree.
            return false;
        }

        // If we're down to our last character, check if we're also at the last
        // character of a word. If we are, remove the key from the tree.
        // Otherwise, return false because the key isn't in the
        // tree.
        if (key.length() == 1)
        {
            if (child.isWordEnd())
            {
                // Clean up the word end logic.
                child.decrementRefCount();
                child.setWordEndFalse();

                // Decrement the reference count for the previous characters.
                // Only remove characters from the childrenNodes maps if they
                // are the last references.
                if (child.getRefCount() == 0)
                {
                    parentNode.childrenNodes.remove(currentCharacter);
                }

                while (!keyCharacters.isEmpty())
                {
                    PairNodeChar previousPair = keyCharacters.pop();
                    Node childNode = previousPair.getNode().childrenNodes.get(previousPair.getChar());

                    childNode.decrementRefCount();
                    if (childNode.getRefCount() == 0)
                    {
                        previousPair.getNode().childrenNodes.remove(previousPair.getChar());
                    }
                }

                --sizeOfTrie;
                return true;
            }

            return false;
        }

        // If we're not down to our last character, pair the parentNode with the
        // character and add the pair to a stack so we can remove it later if
        // necessary.
        keyCharacters.push(new PairNodeChar(parentNode, currentCharacter));

        // Pass in the rest of the key substring for another cycle.
        return remove(key.substring(1), child, keyCharacters);
    }

    /**
     * This method clears the entire tree.
     */
    public void removeAll()
    {
        rootNode.childrenNodes.clear();
        sizeOfTrie = 0;
    }

    /**
     * This method returns a HashMap<String, Value> collection that contain the
     * prefix input in the trie tree.
     * 
     * @param prefix
     *            String of the prefix.
     * @return HashMap<String, Value> collection of all entries that match the
     *         prefix in the trie tree.
     */
    public HashMap<String, Value> keyValueCollectionWithPrefix(String prefix)
    {
        HashMap<String, Value> keyCollection = new HashMap<String, Value>();

        // Retrieve node at the end of the prefix, if it exists.
        Node n = new Node();
        if (prefix == null)
            return keyCollection;
        else if (prefix == "")
            n = rootNode;
        else
        {
            n = get(prefix, rootNode, true);
            if (n == null)
                return keyCollection;
        }

        addToKVPrefixCollection(new StringBuilder(prefix), n, keyCollection);
        return keyCollection;
    }

    /**
     * This method adds a <String, Value> pair that contains the prefix to the
     * key-value collection.
     * 
     * @param prefix
     *            String of the prefix.
     * @param parentNode
     *            Node that contains the current Character.
     * @param keyCollection
     *            HashMap<String, Value> that contains all keys and values that
     *            match the prefix.
     */
    private void addToKVPrefixCollection(StringBuilder prefix, Node parentNode,
        HashMap<String, Value> keyCollection)
    {
        // If this prefix is a word, add the key/value to the collection.
        if (parentNode.isWordEnd())
            keyCollection.put(prefix.toString(), parentNode.getValue());

        // Acquire the children in the map. Then go through the children,
        // appending the characters to the prefix and pass recursively.
        Set<Map.Entry<Character, Node>> childrenSet = parentNode.childrenNodes.entrySet();
        for (Map.Entry<Character, Node> child : childrenSet)
        {
            prefix.append(child.getKey());
            // Not performing tail recursion may result in stack overflow if
            // the trie tree is extremely large and sparse.
            addToKVPrefixCollection(prefix, child.getValue(), keyCollection);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * This method returns a HashMap<String, Value> collection of all entries in
     * the trie tree.
     * 
     * @return HashMap<String, Value> collection of all entries in the trie
     *         tree.
     */
    public HashMap<String, Value> allKeyValues()
    {
        return keyValueCollectionWithPrefix("");
    }
}
