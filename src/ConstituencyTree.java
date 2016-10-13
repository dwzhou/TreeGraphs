import java.util.StringTokenizer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
//import com.mxgraph.layout.mxCompactTreeLayout;

@SuppressWarnings("serial")
public class ConstituencyTree extends JFrame {
	mxGraph graph = new mxGraph();

	public ConstituencyTree(String input, String output) throws IOException {
		Node tree = parseTree(input);

		graph = new mxGraph();
		Object parent = graph.getDefaultParent();

		mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
		//layout.setPrefVertEdgeOff(0);
		//layout.setLevelDistance(50);

		graph.setAutoSizeCells(true);

		graph.getModel().beginUpdate();
		try {
			Object root = graph.insertVertex(parent, null, tree.getData(), 0, 0, 0, 0,
					"defaultVertex;fontColor=black;strokeColor=white;fillColor=white");

			graph.updateCellSize(root);
			for (Node each : tree.getChildren()) {
				createTree(each, root);
			}

			layout.execute(parent, root);

		} finally {
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		getContentPane().add(graphComponent);
		graphComponent.getViewport().setOpaque(true);
		graphComponent.getViewport().setBackground(Color.WHITE);

		BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
		ImageIO.write(image, "PNG", new File(output));
	}

	private void createTree(Node tree, Object root) {
		Object parent = graph.getDefaultParent();

		// create vertex for root
		Object v1 = graph.insertVertex(parent, null, tree.getData(), 0, 0, 0, 0,
				"defaultVertex;fontColor=black;strokeColor=white;fillColor=white");
		graph.updateCellSize(v1);
		graph.insertEdge(parent, null, "", root, v1, "defaultEdge;strokeColor=black");

		// create vertex for each child
		for (Node each : tree.getChildren()) {
			createTree(each, v1);
		}

	}

	public static Node parseTree(String input) {
		Node root = null;

		// tokenize input string using [ and ] as delimiters
		StringTokenizer st = new StringTokenizer(input, "[]", true);

		while (st.hasMoreTokens()) {
			String next = st.nextToken();

			if (next.equals("[")) {
				// start new parent node
				continue;
			} else if (next.equals("]")) {
				// end of parent node, go up one level
				if (root.getParent() != null) {
					root = root.getParent();
				}
			} else {
				// new token, add new child node
				if (root == null) {
					// create root node if null
					root = new Node(null, next);
				} else {
					// add child node
					Node token = new Node(root, next);
					root.getChildren().add(token);
					root = token;
				}
			}
		}

		return root;
	}

	public static void main(String[] args) throws IOException {
		String usage = "Usage:\tjava ConstituencyTree [-input String] [-output String]"
				+ "\n[-input] = An input string in the form [Root [Child1 [Child of Child1]]]"
				+ "\n[-output] = Full path with filename of output PNG file";
		if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
			System.out.println(usage);
			System.exit(0);
		}

		// set default variable values
		String input = "";
		String output = "";

		// change default variables to those input by user
		for (int i = 0; i < args.length; i++) {
			if ("-input".equals(args[i])) {
				input = args[i + 1];
				i++;
			} else if ("-output".equals(args[i])) {
				output = args[i + 1];
				i++;
			}
		}

		// parse input string to check for correct number of brackets
		int numOpenBrackets = 0;
		int numClosedBrackets = 0;
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '[') {
				numOpenBrackets++;
			}
			if (input.charAt(i) == ']') {
				numClosedBrackets++;
			}
		}

		if (input.length() == 0 || output.length() == 0) {
			System.out.println("Input and/or output is empty. Please specify an input string and output location.");
		} else if (numOpenBrackets != numClosedBrackets) {
			System.out.println("Number of open brackets [ does not match number of closing brackets ]."
					+ "\nPlease check your input string and try again.");
		} else {
			ConstituencyTree frame = new ConstituencyTree(input, output);

			// open JFrame
			/*
			 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			 * frame.setSize(400, 320); frame.setVisible(true);
			 */
		}

	}

}
