import java.util.ArrayList;
import java.util.List;

//source: http://programtalk.com/java/java-tree-implementation/
public class Node {
	private String data;
	private List<Node> children = new ArrayList<>();
	private final Node parent;

	public Node(Node parent, String data) {
		this.parent = parent;
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<Node> getChildren() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

}