package tsp

import scala.Array.canBuildFrom

import com.mxgraph.canvas.mxSvgCanvas
import com.mxgraph.util.mxCellRenderer
import com.mxgraph.util.mxCellRenderer.CanvasFactory
import com.mxgraph.util.mxConstants
import com.mxgraph.util.mxDomUtils
import com.mxgraph.util.mxUtils
import com.mxgraph.util.mxXmlUtils
import com.mxgraph.view.mxGraph

object GraphVisualizer {
  val VERTEX_STYLE_1 = "defaultVertex;strokeColor=red;fillColor=white;rounded=true"
  val VERTEX_STYLE_2 = "shape=ellipse;strokeColor=black;fillColor=black"
  val VERTEX_STYLE = VERTEX_STYLE_2
  val START_VERTEX_STYLE = VERTEX_STYLE
  val EDGE_STYLE = "endArrow=none;"
  val vertexSize = 0
  val width = 600
  val height = 600

  def visualize(context: Context, name:String = "graph") {
    val edges = context.tour
    val distCalc = context.distCalc
    val graph = new mxGraph()
    var vertices = new Array[Any](context.nodeCount)
    try {
      graph.getModel().beginUpdate()
      //vertices
      val (maxx, maxy) = context.nodes.foldRight((0.0, 0.0))((a, b) => (Math.max(a(0), b._1), Math.max(a(1), b._2)));
      var i = -1
      val start = edges.head
      for (node <- context.nodes) yield {
        i += 1
        val style = if (start == i) START_VERTEX_STYLE else VERTEX_STYLE
        vertices(i) = graph.insertVertex(graph.getDefaultParent(), /*i.toString*/ null, /*i*/null, node(0) * width / maxx, node(1) * height / maxy, vertexSize, vertexSize, style)
      }
      
      //edges
      def insertEdge(from:Int, to:Int) = {
        graph.insertEdge(graph.getDefaultParent(), null, /*distCalc.coordinateDistance(context.nodes(edges(from)), context.nodes(edges(to))) */null, vertices(edges(from)), vertices(edges(to)), EDGE_STYLE)
      }
        
      for (idx <- 1 until context.nodeCount) {
        insertEdge(idx - 1, idx)
      }
      insertEdge(context.nodeCount - 1, 0)
    } finally {
      graph.getModel().endUpdate()
    }
    //export
    val userDir = System.getProperty("user.dir")
    exportSvg(graph, s"$userDir/$name.svg")
  }

  def exportSvg(graph: mxGraph, filename: String) {
    val canvas: mxSvgCanvas =
      mxCellRenderer.drawCells(graph, null, 1, null,
        new CanvasFactory() {
          def createCanvas(width: Int, height: Int): mxSvgCanvas = {
            val canvas = new mxSvgCanvas(mxDomUtils.createSvgDocument(width, height));
            canvas.setEmbedded(true);
            return canvas;
          }
        }).asInstanceOf[mxSvgCanvas];
    mxUtils.writeFile(mxXmlUtils.getXml(canvas.getDocument()), filename);
  }
}