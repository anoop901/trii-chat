joint.shapes.msg = {}, joint.shapes.msg.Member = joint.dia.Element.extend({
    markup: '<g class="rotatable"><g class="scalable"><rect class="card"/></g><text class="author"/><text class="content"/></g>',
    defaults: joint.util.deepSupplement({
        type: "msg.Member",
        size: {
            width: 180,
            height: 70
        },
        attrs: {
            rect: {
                width: 170,
                height: 60
            },
            ".card": {
                fill: "#FFFFFF",
                stroke: "#000000",
                "stroke-width": 0,
                "pointer-events": "visiblePainted",
                rx: 10,
                ry: 10
            },
            ".author": {
                ref: ".card",
                "ref-x": .5,
                "y": 20,
                'x-alignment': 'middle',
                //"font-family": "Courier New",
                "font-size": 14,
                //"text-anchor": "end"
            },
            ".content": {
                ref: ".card",
                //"font-weight": "800",
                "ref-x": .1,
                "y": 45,
                //"font-family": "Courier New",
                //'y-alignment': 'middle',
                "font-size": 14,
                //"text-anchor": "end"
            }
        }
    }, joint.dia.Element.prototype.defaults)
}), joint.shapes.msg.Arrow = joint.dia.Link.extend({
    defaults: {
        type: "msg.Arrow",
        source: {
            selector: ".card"
        },
        target: {
            selector: ".card"
        },
        attrs: {
            ".connection": {
                stroke: "#585858",
                "stroke-width": 3
            }
        },
        z: -1
    }
});

joint.shapes.msg.Member.prototype.markup = [
    '<g class="rotatable">',
	    '<g class="scalable">',
	    	'<rect class="card"/>',
	    '</g>',
	    '<text class="author"/><text class="content"/>',
    '</g>'
].join('');

var member = function(id, username, message, background, textColor) {

    textColor = textColor || "#000";
    //var content = message + '\n';
    var content = joint.util.breakText(message, { width: 170 })
    
//    var maxLineLength = _.max(label.split('\n'), function(l) { return l.length; }).length;
//
//    // Compute width/height of the rectangle based on the number 
//    // of lines in the label and the letter size. 0.6 * letterSize is
//    // an approximation of the monospace font letter width.
    var letterSize = 15;
//    var width = 2 * (letterSize * (0.6 * maxLineLength + 1));
    var Height = (content.split('\n').length) * letterSize;
    
    var cell = new joint.shapes.msg.Member({
    	id: id,
    	size: { width: 180, height: 45 + Height },
        attrs: {
            '.card': { fill: background },
            '.author': { fill: textColor, text: joint.util.breakText(username, { width: 170 }) },
            '.content': { fill: textColor, text: content },
        }
    })
    
    //cell.resize(180, 45 + Height);
    
    return cell;
};

function link(source, target) {

    var cell = new joint.shapes.msg.Arrow({
        source: { id: source.id },
        target: { id: target.id }
    });

    return cell;
}

var graph;
var paper;
var paperScroller;
var graphLayout;
var treeLayoutView;

function createMessages() {
    
    // Create paper and graph.
    // ------------------------------------

    graph = new joint.dia.Graph;
    paper = new joint.dia.Paper({
        width: 1,
        height: 1,
        gridSize: 1,
        model: graph,
        interactive: false,
        defaultLink: new joint.shapes.msg.Arrow()
    });

    paperScroller = new joint.ui.PaperScroller({
        paper: paper,
        autoResizePaper: true
    });
    
    paper.on('blank:pointerdown', paperScroller.startPanning);
    paperScroller.$el.css({ width: '100%', height: '66%' }).appendTo('#paper-holder');

    graph.resetCells();
    
    graphLayout = new joint.layout.TreeLayout({
        graph: graph,
        direction: 'B',
        verticalGap: 20,
        horizontalGap: 40
    });

    graphLayout.layout();
    paperScroller.centerContent();
    $('.paper-scroller').css("padding-top", "5px");
    $('.paper').css("margin-bottom", "5px");
    
    $('#btn-layout').on('click', function() {

        graphLayout.layout();
        paperScroller.centerContent();
        $('.paper-scroller').css("padding-top", "5px");
        $('.paper').css("margin-bottom", "5px");
    });
    
    graph.on('change', function(cell) { 
        graphLayout.layout();
        paperScroller.centerContent();
        $('.paper-scroller').css("padding-top", "5px");
        $('.paper').css("margin-bottom", "5px");
    });
    
    
    //paperScroller.zoom(-0.3);
    $('.paper-scroller').css("padding-top", "5px");
    $('.paper').css("margin-bottom", "5px");

    paper.on('cell:pointerup', function(cellView, evt, x, y) {
    	if(V(evt.target).hasClass('selected')){
	    	var removeItem = cellView.model.id;  
	    	selectedMessageIDs = $.grep(selectedMessageIDs, function(value) {
	    	  return value != removeItem;
	    	});
    		V(evt.target).removeClass('selected');
	    	cellView.model.attr({
	            '.card': { 
	                stroke: "#000000",
	                "stroke-width": 0
	             }
    		});
	    	if(!selectedMessageIDs.length){
	    		$('#message-send-form').remove();
	    	}
	    		
	    } else {
	    	if(!selectedMessageIDs.length){
	    		displayMessageForm();
	    	}
    		selectedMessageIDs.push(cellView.model.id);
	    	V(evt.target).addClass('selected');
	    	cellView.model.attr({
	            '.card': { 
	                stroke: "#2DB303",
	                "stroke-width": 4
	             }
    		});
	    }
    	evt.stopPropagation();
    });
//    paper.on('cell:pointerup', function(cellView, evt, x, y) {
//
//        if (V(evt.target).hasClass('add')) {
//
//            var newMember = member('Employee', 'New Employee', '/images/demos/orgchart/female.png', '#c6c7e2');
//            var newConnection = link(cellView.model, newMember);
//            graph.addCells([newMember, newConnection]);
//            graphLayout.prepare().layout();
//            paperScroller.centerContent();
//            $('.paper-scroller').css("padding-top", "5px");
//            $('.paper').css("margin-bottom", "5px");
//
//        } else if (V(evt.target).hasClass('del')) {
//
//            cellView.model.remove();
//            graphLayout.prepare().layout();
//
//        } else if (V(evt.target).hasClass('edit')) {
//
//            var inspector = new joint.ui.Inspector({
//                inputs: {
//                    'rank': {
//                        type: 'text',
//                        label: 'Rank',
//                        index: 1
//                    },
//                    'name': {
//                        type: 'text',
//                        label: 'Name',
//                        index: 2
//                    },
//                    'attrs/image/xlink:href': {
//                        type: 'select',
//                        label: 'Sex',
//                        options: [
//                            { value: '/images/demos/orgchart/male.png', content: 'Male' },
//                            { value: '/images/demos/orgchart/female.png', content: 'Female' }
//                        ],
//                        index: 3
//                    },
//                    'attrs/.card/fill': {
//                        type: 'color-palette',
//                        label: 'Color',
//                        index: 5,
//                        options: [
//                            { content: '#31d0c6' },
//                            { content: '#7c68fc' },
//                            { content: '#fe854f' },
//                            { content: '#feb663' },
//                            { content: '#c6c7e2' }
//                        ]
//                    }
//                },
//                cellView: cellView
//            });
//
//            var dialog = new joint.ui.Dialog({
//                width: 250,
//                title: 'Edit Member',
//                content: inspector.render().el
//            });
//
//            dialog.on('action:close', inspector.remove, inspector);
//            dialog.open();
//        }
//    });
//
    
}

function addMessage(message) {
	var newMember = member(message['id'].toString(), message['author'], message['body'], '#c6c7e2', '');
    graph.addCell(newMember);
    for (var i = 0; i < message['parents'].length; i++) {
        var parentID = message['parents'][i];
        var parent = graph.getCell(parentID.toString());
        graph.addCell(link(parent,newMember));
    }
    graphLayout.prepare().layout();
}