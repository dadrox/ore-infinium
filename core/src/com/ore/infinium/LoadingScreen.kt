/**
MIT License

Copyright (c) 2016 Shaun Reich <sreich02@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.ore.infinium

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisProgressBar
import com.kotcrab.vis.ui.widget.VisScrollPane
import com.kotcrab.vis.ui.widget.VisTable
import com.ore.infinium.util.scrollToBottom
import kotlinx.coroutines.experimental.channels.ProducerJob

class LoadingScreen(private val client: OreClient,
                    private val ownStage: Stage,
                    rootTable: VisTable) : VisTable() {

    val loadingMeter = VisProgressBar(0f, 100f, 1f, false)

    val airAmountLabel = VisLabel()

    //   val progressText: VisTextArea
    val progressScroll: VisScrollPane
    val progressScrollTable: VisTable

    val progressElements = mutableListOf<VisLabel>()

    val fixedCenterTable: VisTable

    init {
        loadingMeter.value = 0f

        val loadingLabel = VisLabel("LOADING SCREEN")
//        progressText = VisTextArea("progress12345")

        progressScrollTable = VisTable()
        progressScroll = VisScrollPane(progressScrollTable)

        fixedCenterTable = VisTable().apply {
            add(loadingLabel).padRight(1f)
            row()
            add(loadingMeter)
        }

        add(fixedCenterTable).center()
//hack        add(progressScroll).expand().fill().colspan(1)
        row()
        add(progressScroll).fill().size(600f, 300f)
        //progressScroll.setScrollingDisabled(true, true)
        progressScroll.setFadeScrollBars(false)
        progressScroll.setScrollBarPositions(true, true)

        progressScroll.scrollPercentY = 100f
//        rootTable.add(this).expand().fill() //.padBottom(5f)//.size(500f, 200f)

//
//        TextArea textArea = new TextArea("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec iaculis odio.",
//                                         skin);
//        textArea.setPrefRows(5);
//
//        // ---
//
//        VisTable table = new VisTable();
//
//        for (int i = 0; i < 20; i++)
//        table.add(new Label ("Label #" + (i + 1), skin)).expand().fill().row();
//
//        ScrollPane scrollPane = new ScrollPane(table, skin, "list");
//        scrollPane.setFlickScroll(false);
//        scrollPane.setFadeScrollBars(false);
//
//        // ---
//`
//        add(textArea).row();
//        add(scrollPane).spaceTop(8).fillX().expandX().row();

//        progressText.layout()
        progressScroll.layout()
        //progressScroll.scrollToBottom()
        setBackground("grey-panel")
        this.layout()
    }

    fun addNewProgressLine(text: String) {
        val element = VisLabel(text)
        progressElements.add(element)

        progressScrollTable.row().left()//.pad(1f)

        progressScrollTable.apply {
            add(element).expandX().fill()
        }

        element.layout()
        progressScrollTable.layout()
        progressScroll.scrollToBottom()
        this.layout()
    }

    fun progressReceived(progress: String, worldGenJob: ProducerJob<String>) {
        loadingMeter.value += 1
        if (loadingMeter.value >= 100) {
            loadingMeter.value -= 100
        }

        addNewProgressLine(progress)

        if (worldGenJob.isCompleted) {
            addNewProgressLine("finished!")
        }

    }
}

