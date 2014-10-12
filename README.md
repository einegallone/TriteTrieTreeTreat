TriteTrieTreeTreat
=================
Generic prefix trie tree data structure that utilizes hashmaps to link its branches. Each key is represented by a String and is associated with a Value. Each character of the key String is a node and only the nodes that represent the ends of keys have Values associated with them.

Version 1.0.1
===========
Public methods:

1. long size()
This method returns the number of keys in the tree.
    @return
        Number of keys in the tree.

2. boolean isEmpty()
This method reports whether the tree is empty.
    @return
        True if the true is empty. False otherwise.

3. Value get(String key)
This method gets the Value associated with the key, if the key exists in the tree.
    @param key
        String of the key.
    @return
        Value associated with the key. Null if the key does not exist or no Value associated with key.

4. boolean contains(String key)
This method checks if a key is in the trie tree.
    @param key
        String of the key.
    @return
        True if the key is in the trie tree. False if it is not.

5. boolean put(String key, Value val)
This method puts a string into the trie and associates a Value with the string. Duplicates will not be added.
    @param key
        String of the key.
    @param val
        Value to associate with string.
    @return
        True if put was successful. False if the key is empty or trie already contains key.

6. boolean update(String key, Value val)
This method updates the Value associated with the key.
    @param key
        String of the key.
    @param val
        Value to associate with string.
    @return
        True if update was successful. False if the key is empty or trie does not contain key.

7. boolean remove(String key)
This method removes a key from the tree, if it exists.
    @param key
        String of the key.
    @return
        True if the key is removed. False if the key is not in the tree.
        
8. boolean removeAll()
This method clears the entire tree.

9. HashMap<String, Value> keysWithPrefix(String prefix)
This method returns a HashMap<String, Value> collection that contain the prefix input in the trie tree.
    @param prefix
        String of the prefix.
    @return
        HashMap<String, Value> collection of all entries that match the prefix in the trie tree.

10. HashMap<String, Value> allKeys()
This method returns a HashMap<String, Value> collection of all entries in the trie tree.
    @return
        HashMap<String, Value> collection of all entries in the trie tree.

Unit Tests
===========
Unit tests cover 100% of the functions and 94.9% of the conditions. All lines have been touched.
Missing conditions include:
- asserts for false cases
- public void Node::decrementRefCount(), where there's a safeguard on referenceCount, preventing it from becoming negative