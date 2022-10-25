package org.github.ainr.graphs

import cats.syntax.all._
import cats.effect.IO
import org.nspl
import org.nspl.awtrenderer
import org.nspl.awtrenderer._

import java.io.File

trait Graphs {

  def plot(input: Input): IO[File]
}

object Graphs {

  def apply(): Graphs = new Graphs {

    override def plot(input: Input): IO[File] = for {
      plots <- input.data
        .parTraverse {
          data => getPlot(data.seq, data.main, data.xlab, data.ylab, data.color)
        }
      sequence <- IO.delay(
        nspl.sequence(
          plots,
          nspl.TableLayout(plots.size)
        )
      )
      realTime <- IO.realTime
      file <- IO.delay(
        awtrenderer.renderToFile(
          f = File.createTempFile(
            s"nspl_plot_${realTime.toMicros}",
            ".png"
          ),
          elem = sequence,
          width = 2000,
          mimeType = "image/png"
        )
      )
    } yield file

    private def getPlot(
        data: Seq[Double],
        main: String,
        xlab: String,
        ylab: String,
        color: nspl.Color
    ): IO[nspl.Elems2[nspl.XYPlotArea, nspl.Legend]] = IO.delay {
      nspl.xyplot(
        data -> nspl.bar(
          stroke =
            nspl.StrokeConf(
              nspl.RelFontSize(0.1),
              nspl.CapRound
            ),
          fill = nspl.Color.rgbInterpolate(
            nspl.Color.transparent, color, 0.5
          ),
          strokeColor = nspl.Color.gray1
        )
      )(parameters =
        nspl.par(
          main = main,
          xlab = xlab,
          ylab = ylab
        )
      ).build
    }
  }
}
