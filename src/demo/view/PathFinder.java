package demo.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import graph.matrix.AStarCostEvaluator;
import graph.model.ICostEvaluator;
import demo.model.XAStarPathAlgorithm;
import demo.model.XMatrix;
import demo.model.XNode;




public class PathFinder {

	@SuppressWarnings("serial")
	static class CanvasPanel extends JPanel {

		JLabel statusLabel = new JLabel(" ", JLabel.CENTER);
		
		public void setStatus(String text) {
			if (text != null && text.length() > 0 ) {
				statusLabel.setText(text);
			}
			else {
				statusLabel.setText(" ");
			}
		}
		
		public CanvasPanel(String name, Canvas canvas) {
			this.setLayout(new BorderLayout());
			JLabel titleLabel = new JLabel(name, JLabel.CENTER);
			add(BorderLayout.NORTH, titleLabel);
			add(BorderLayout.CENTER, canvas);
			add(BorderLayout.SOUTH, statusLabel);
		}

	}

	Canvas createCanvas(final Parameters parameters, final ICostEvaluator evaluator) {
		final XMatrix matrix = new XMatrix();
		matrix.setEvaluator(evaluator);
		final Canvas canvas = new Canvas();
		canvas.setParameters(parameters);
		canvas.setMatrix(matrix);
		matrix.setNodeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (parameters.getAnimationMs() == 0) {
					return;
				}
				try {
					canvas.repaint();
					Thread.sleep(parameters.getAnimationMs());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		return canvas;
	}

	public void start() {
		final Parameters parameters = new Parameters();
		final ControlPanel controlPanel = new ControlPanel(parameters);

		final Coordinator coordinator = new Coordinator();
		coordinator.setControlPanel(controlPanel);

		final AStarCostEvaluator evaluator1 = new AStarCostEvaluator();
		final XAStarPathAlgorithm pathAlgorithm1 = new XAStarPathAlgorithm();
		pathAlgorithm1.setEvaluator(evaluator1);
		final Canvas canvas1 = createCanvas(parameters, evaluator1);
		final CanvasPanel canvasPanel1 = new CanvasPanel("A*", canvas1);
	
		coordinator.add(canvas1, pathAlgorithm1, evaluator1);
		PropertyChangeListener propertyListener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				Object source = evt.getSource();
				String propertyName = evt.getPropertyName();
				if (propertyName.equals(AppConstant.SearchStarted)) {
					if (source == canvas1) {
						canvasPanel1.setStatus(null);
					}
				}
				else if (propertyName.equals(AppConstant.SearchCompleted)) {
					if (source == canvas1) {
						canvasPanel1.setStatus(format(canvas1.getMatrix()));
					}
				}
			}
			
			String format(XMatrix matrix) {
				StringBuilder sb = new StringBuilder();
				sb.append("cost: ");
				sb.append(matrix.getEnd().getCost());
				sb.append(", visited: ");
				sb.append(countVisistedNodes(matrix));
				return sb.toString();
			}
			
			int countVisistedNodes(XMatrix matrix) {
				int count = 0;
				for (int row = 0; row < matrix.getRows(); row++) {
					for (int col = 0; col < matrix.getColumns(); col++) {
						XNode node = matrix.getValue(row, col);
						if (node.isVisited()) {
							count++;
						}
					}
				}
				return count;
			}
			
		};
		coordinator.addPropertyChangeListener(canvas1, propertyListener);

		JPanel overallPanel = new JPanel(new BorderLayout());
		overallPanel.add(controlPanel, BorderLayout.WEST);
		JPanel mainPanel = new JPanel(new GridLayout(1,0,10,10));
		mainPanel.add(canvasPanel1);
		overallPanel.add(mainPanel, BorderLayout.CENTER);

		JFrame frame = new JFrame("Path Finder");
		frame.getContentPane().add(overallPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(840, 420));
		frame.setSize(1200, 600);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}


