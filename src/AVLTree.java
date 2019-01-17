public class AVLTree {

    /**********************************************************
     * AVL tree ADT.
     * Supported operations:
     * Insert
     * Delete
     * Find
     * Min
     * Max
     **********************************************************/

    public AVLTreeNode root;   /* Pointer to the root of the tree */
    public int noOfNodes;       /* No of nodes in the tree */

    public AVLTree() {
    }

    /*******************************************************
     * Returns the number of nodes in the tree
     *******************************************************/
    int NoOfNodes() {
        return noOfNodes;
    }

    /*******************************************************
     * Inserts the key into the AVLTree. Returns a pointer
     * to the newly inserted node
     *******************************************************/
    AVLTreeNode Insert(int key) {
        root = insert(root, key);
        return null;
    } //end-Insert

    // A utility function to get maximum of two integers
    int maxOf(int a, int b) {
        return (a > b) ? a : b;
    } //end-maxOf

    private AVLTreeNode insert(AVLTreeNode node, int key) {
        /* 1.  Perform the normal BST insertion */
        if (node == null) {
            noOfNodes++;
            return (new AVLTreeNode(key));
        }

        if (key < node.key)
            node.left = insert(node.left, key);
        else if (key > node.key)
            node.right = insert(node.right, key);
        else // Duplicate keys not allowed
            return node;

        /* 2. Update height of this ancestor node */
        node.height = 1 + maxOf(height(node.left),
                height(node.right));

        /* 3. Get the balance factor of this ancestor
              node to check whether this node became
              unbalanced */
        int balance = BalanceFactor(node);

        // If this node becomes unbalanced, then there
        // are 4 cases Left Left Case
        if (balance > 1 && key < node.left.key)
            return rightRotate(node);

        // Right Right Case
        if (balance < -1 && key > node.right.key)
            return leftRotate(node);

        // Left Right Case
        if (balance > 1 && key > node.left.key) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Left Case
        if (balance < -1 && key < node.right.key) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        /* return the (unchanged) node pointer */
        return node;
    } //end-insert

    private AVLTreeNode leftRotate(AVLTreeNode x) {
        AVLTreeNode y = x.right;
        AVLTreeNode T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        //  Update heights
        x.height = Math.max(height(x.left), height(x.right))+1;
        y.height = Math.max(height(y.left), height(y.right))+1;

        // Return new root
        return y;
    } //end-leftRotate

    private AVLTreeNode rightRotate(AVLTreeNode y) {
        AVLTreeNode x = y.left;
        AVLTreeNode T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights
        y.height = Math.max(height(y.left), height(y.right))+1;
        x.height = Math.max(height(x.left), height(x.right))+1;

        // Return new root
        return x;
    } //end-rightRotate

    /*******************************************************
     * Deletes the key from the tree (if found). Returns
     * 0 if deletion succeeds, -1 if it fails
     *******************************************************/
    int Delete(int key) {
        if(Find(key) == null) return -1;

        if(root != null && root.right == null && root.left == null){
            noOfNodes = noOfNodes - 1;
            root = null;
            return 0;
        }

        root = deleteNode(root, key);
        if (root == null) return -1;
        else{
            noOfNodes = noOfNodes - 1;
            return 0;
        }
    }

    private AVLTreeNode deleteNode(AVLTreeNode root, int value) {
        // STEP 1: PERFORM STANDARD BST DELETE

        if (root == null)
            return root;

        // If the value to be deleted is smaller than the root's value,
        // then it lies in left subtree
        if ( value < root.key )
            root.left = deleteNode(root.left, value);

            // If the value to be deleted is greater than the root's value,
            // then it lies in right subtree
        else if( value > root.key )
            root.right = deleteNode(root.right, value);

            // if value is same as root's value, then This is the node
            // to be deleted
        else {
            // node with only one child or no child
            if( (root.left == null) || (root.right == null) ) {

                AVLTreeNode temp;
                if (root.left != null)
                    temp = root.left;
                else
                    temp = root.right;

                // No child case
                if(temp == null) {
                    temp = root;
                    root = null;
                }
                else // One child case
                    root = temp; // Copy the contents of the non-empty child

                temp = null;
            }
            else {
                // node with two children: Get the inorder successor (smallest
                // in the right subtree)
                AVLTreeNode temp = minValueNode(root.right);

                // Copy the inorder successor's data to this node
                root.key = temp.key;

                // Delete the inorder successor
                root.right = deleteNode(root.right, temp.key);
            }
        }

        // If the tree had only one node then return
        if (root == null)
            return root;

        // STEP 2: UPDATE HEIGHT OF THE CURRENT NODE
        root.height = Math.max(height(root.left), height(root.right)) + 1;

        // STEP 3: GET THE BALANCE FACTOR OF THIS NODE (to check whether
        //  this node became unbalanced)
        int balance = BalanceFactor(root);

        // If this node becomes unbalanced, then there are 4 cases

        // Left Left Case
        if (balance > 1 && BalanceFactor(root.left) >= 0)
            return rightRotate(root);

        // Left Right Case
        if (balance > 1 && BalanceFactor(root.left) < 0) {
            root.left =  leftRotate(root.left);
            return rightRotate(root);
        }

        // Right Right Case
        if (balance < -1 && BalanceFactor(root.right) <= 0)
            return leftRotate(root);

        // Right Left Case
        if (balance < -1 && BalanceFactor(root.right) > 0) {
            root.right = rightRotate(root.right);
            return leftRotate(root);
        }

        return root;
    }

    private AVLTreeNode minValueNode(AVLTreeNode node) {
        AVLTreeNode current = node;
        /* loop down to find the leftmost leaf */
        while (current.left != null)
            current = current.left;
        return current;
    }

    /*******************************************************
     * Searches the AVLTree for a key. Returns a pointer to the
     * node that contains the key (if found) or NULL if unsuccessful
     *******************************************************/
    AVLTreeNode Find(int key) {
        AVLTreeNode p = root;
        while (p != null){
            if (key == p.key) return p;
            else if (key < p.key) p = p.left;
            else /* key > p.key */ p = p.right;
        } /* end-while */
        return null;
    } //end-Find

    /*******************************************************
     * Returns a pointer to the node that contains the minimum key
     *******************************************************/
    AVLTreeNode Min() {
        if (root == null)
            return null;
        AVLTreeNode p = root;
        while (p.left != null){
            p = p.left;
        } //end-while
        return p;
    } //end-Min

    /*******************************************************
     * Returns a pointer to the node that contains the maximum key
     *******************************************************/
    AVLTreeNode Max() {
        if (root == null)
            return null;
        AVLTreeNode p = root;
        while (p.right != null){
            p = p.right;
        } //end-while
        return p;
    } //end-Max

    /*******************************************************
     * Performs an inorder traversal of the tree and prints [key, height, bf]
     * triplets in sorted order in a nicely formatted table
     *******************************************************/
    void Print() {
        InorderTraversal(root);
    } //end-Print

    private void InorderTraversal(AVLTreeNode node){
        if (node == null) return;
        InorderTraversal(node.left);
        System.out.println("[" + node.key + ", " + node.height + ", " + BalanceFactor(node) + "]");
        InorderTraversal(node.right);
    } //end-InorderTraversal

    // Get Balance factor of the node
    private int BalanceFactor(AVLTreeNode node){
        if (node == null)
            return 0;

        return height(node.left) - height(node.right);
    } //end-BalanceFactor

    private int height(AVLTreeNode a) {
        if (a == null) {
            return -1;
        }
        return a.height;
    } //end-height
}

