joint.shapes.org.Member.prototype.markup = [
    '<g class="rotatable">',
    '<g class="scalable">',
    '<rect class="card"/><image/>',
    '</g>',
    '<text class="rank"/><text class="name"/>',
    '<g class="btn add"><circle class="add"/><text class="add">+</text></g>',
    '<g class="btn del"><circle class="del"/><text class="del">-</text></g>',
    '<g class="btn edit"><rect class="edit"/><text class="edit">EDIT</text></g>',
    '</g>'
].join('');

var member = function(username, message, image, background, textColor) {

    textColor = textColor || "#000";

    var cell = new joint.shapes.org.Member({
        size: { width: 260, height: 90 },
        attrs: {
            '.card': { fill: background, 'stroke-width': 0 },
            image: { 'xlink:href': image, 'ref-y': 10, opacity: 0.7 },
            '.rank': { fill: textColor, text: '', 'font-size': 13, 'text-decoration': 'none', 'ref-x': 0.95, 'ref-y': 0.5, 'y-alignment': 'middle', 'word-spacing': '-5px', 'letter-spacing': 0 },
            '.name': { fill: textColor, text: '', 'ref-x': 0.95, 'ref-y': 0.62, 'font-family': 'Arial' },
            '.btn.add': { 'ref-dx': -15,'ref-y': 15, 'ref': '.card' },
            '.btn.del': { 'ref-dx': -45,'ref-y': 15, 'ref': '.card' },
            '.btn.edit': { 'ref-dx': -140,'ref-y': 5, 'ref': '.card' },
            '.btn>circle': { r: 10, fill: 'transparent', stroke: '#333', 'stroke-width': 1 },
            '.btn>rect': { height: 20, width: 45, rx: 2, ry: 2, fill: 'transparent', 'stroke-width': 1 },
            '.btn.add>text': { fill: textColor,'font-size': 23, 'font-weight': 800, stroke: "#000", x: -6.5, y: 8, 'font-family': 'Times New Roman' },
            '.btn.del>text': { fill: textColor,'font-size': 28, 'font-weight': 500, stroke: "#000", x: -4.5, y: 6, 'font-family': 'Times New Roman' },
            '.btn.edit>text': { fill: textColor,'font-size': 15, 'font-weight': 500, stroke: "#000", x: 5, y: 15, 'font-family': 'Sans Serif' }
        }
    }).on({
        'change:name': function(cell, name) {
            cell.attr('.name/text', joint.util.breakText(name, { width: 160, height: 45 }, cell.attr('.name')));
        },
        'change:rank': function(cell, rank) {
            cell.attr('.rank/text', joint.util.breakText(rank, { width: 165, height: 45 }, cell.attr('.rank')));
        }
    }).set({
        name: username,
        rank: message
    });

    return cell;
};

function link(source, target) {

    var cell = new joint.shapes.org.Arrow({
        source: { id: source.id },
        target: { id: target.id }
    });

    return cell;
}

var members = [
    member('Founder & Chairman', 'Pierre Omidyar', '/images/demos/orgchart/male.png', '#31d0c6'),
    member('President & CEO', 'Margaret C. Whitman', '/images/demos/orgchart/female.png', '#31d0c6'),
    member('President, PayPal', 'Scott Thompson', '/images/demos/orgchart/male.png', '#7c68fc'),
    member('President, Ebay Global Marketplaces' , 'Devin Wenig', '/images/demos/orgchart/male.png', '#7c68fc'),
    member('Senior Vice President Human Resources', 'Jeffrey S. Skoll', '/images/demos/orgchart/male.png', '#fe854f'),
    member('Senior Vice President Controller', 'Steven P. Westly', '/images/demos/orgchart/male.png', '#feb663')
];

var connections = [
    link(members[0], members[1]),
    link(members[1], members[2]),
    link(members[1], members[3]),
    link(members[1], members[4]),
    link(members[1], members[5])
];

function makeLink(parentElementLabel, childElementLabel) {
	
	return new joint.dia.Link({
	    source: { id: parentElementLabel },
	    target: { id: childElementLabel },
	    attrs: { 
	        '.marker-target': { d: 'M 4 0 L 0 2 L 4 4 z', fill: '#7c68fc', stroke: '#7c68fc' },
	        '.connection': { stroke: '#7c68fc' }
	    }
	});
	
	
}

function makeElement(label) {

    var maxLineLength = _.max(label.split('\n'), function(l) { return l.length; }).length;

    // Compute width/height of the rectangle based on the number 
    // of lines in the label and the letter size. 0.6 * letterSize is
    // an approximation of the monospace font letter width.
    var letterSize = 8;
    var width = 2 * (letterSize * (0.6 * maxLineLength + 1));
    var height = 2 * ((label.split('x').length + 1) * letterSize);

    return new joint.shapes.basic.Rect({
        id: label,
        size: { width: width, height: height },
        attrs: {
            text: { text: label, 'font-size': letterSize, 'font-family': 'monospace' },
            rect: {
                width: width, height: height,
                rx: 5, ry: 5,
                stroke: '#fe854f',
                'stroke-width': 2,
                fill: '#feb663'
            }
        },
        rankDir: 'R'
    });
}

function buildGraphFromAdjacencyList(adjacencyList) {

    var elements = [];
    var links = [];
    
    _.each(adjacencyList, function(edges, parentElementLabel) {
        elements.push(makeElement(parentElementLabel));

        _.each(edges, function(childElementLabel) {
            links.push(makeLink(parentElementLabel, childElementLabel));
        });
    });

    // Links must be added after all the elements. This is because when the links
    // are added to the graph, link source/target
    // elements must be in the graph already.
    return elements.concat(links);
}

var graph;
var paper;
var paperScroller;
var graphLayout;
var treeLayoutView;

function createMessages() {
    var list = {
        'az': ['b', 'cy'],
        'b': ['fxf', 'a3'],
        'cy': ['ey', 'dxdy', 'a4'],
        'dxdy': ['iy'],
        'ey': ['hy'],
        'fxf': ['g'],
        'g': [],
        'hy': [],
        'iy': ['a2'],
        'a2': ['b2', 'c2'],
        'b2': ['fxf2'],
        'c2': ['ey2', 'dxdy2'],
        'dxdy2': ['iy2'],
        'ey2': ['hy2'],
        'fxf2': ['g2'],
        'g2': [],
        'hy2': [],
        'iy2': [],
        'a3': ['b3', 'c3'],
        'b3': ['fxf3'],
        'c3': ['ey3', 'dxdy3'],
        'dxdy3': ['iy3'],
        'ey3': ['hy3'],
        'fxf3': ['g3'],
        'g3': [],
        'hy3': [],
        'iy3': [],
        'a4': ['b4', 'c4'],
        'b4': ['fxf4'],
        'c4': ['ey4', 'dxdy4'],
        'dxdy4': ['iy4'],
        'ey4': ['hy4'],
        'fxf4': ['g4'],
        'g4': [],
        'hy4': [],
        'iy4': []
    };

    var cells = buildGraphFromAdjacencyList(list);

    // Create paper and populate the graph.
    // ------------------------------------

    graph = new joint.dia.Graph;
    paper = new joint.dia.Paper({
        width: 1,
        height: 1,
        gridSize: 1,
        model: graph,
        interactive: false,
        defaultLink: new joint.shapes.org.Arrow()
    });

    paperScroller = new joint.ui.PaperScroller({
        paper: paper,
        autoResizePaper: true
    });
    
    paper.on('blank:pointerdown', paperScroller.startPanning);
    paperScroller.$el.css({ width: '100%', height: '66%' }).appendTo('#paper-holder');

    //graph.resetCells(cells);
    graph.resetCells(members.concat(connections));
    
    graphLayout = new joint.layout.TreeLayout({
        graph: graph,
        direction: 'B',
        verticalGap: 20,
        horizontalGap: 40
    });

    treeLayoutView = new joint.ui.TreeLayoutView({
        paper: paper,
        model: graphLayout,
        previewAttrs: {
            parent: { rx: 10, ry: 10 }
        }
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
    
    
    paperScroller.zoom(-0.5);

    paper.on('cell:pointerup', function(cellView, evt, x, y) {

        if (V(evt.target).hasClass('add')) {

            var newMember = member('Employee', 'New Employee', '/images/demos/orgchart/female.png', '#c6c7e2');
            var newConnection = link(cellView.model, newMember);
            graph.addCells([newMember, newConnection]);
            graphLayout.prepare().layout();
            paperScroller.centerContent();
            $('.paper-scroller').css("padding-top", "5px");
            $('.paper').css("margin-bottom", "5px");

        } else if (V(evt.target).hasClass('del')) {

            cellView.model.remove();
            graphLayout.prepare().layout();

        } else if (V(evt.target).hasClass('edit')) {

            var inspector = new joint.ui.Inspector({
                inputs: {
                    'rank': {
                        type: 'text',
                        label: 'Rank',
                        index: 1
                    },
                    'name': {
                        type: 'text',
                        label: 'Name',
                        index: 2
                    },
                    'attrs/image/xlink:href': {
                        type: 'select',
                        label: 'Sex',
                        options: [
                            { value: '/images/demos/orgchart/male.png', content: 'Male' },
                            { value: '/images/demos/orgchart/female.png', content: 'Female' }
                        ],
                        index: 3
                    },
                    'attrs/.card/fill': {
                        type: 'color-palette',
                        label: 'Color',
                        index: 5,
                        options: [
                            { content: '#31d0c6' },
                            { content: '#7c68fc' },
                            { content: '#fe854f' },
                            { content: '#feb663' },
                            { content: '#c6c7e2' }
                        ]
                    }
                },
                cellView: cellView
            });

            var dialog = new joint.ui.Dialog({
                width: 250,
                title: 'Edit Member',
                content: inspector.render().el
            });

            dialog.on('action:close', inspector.remove, inspector);
            dialog.open();
        }
    });

    $('.paper-scroller').css("padding-top", "5px");
    $('.paper').css("margin-bottom", "5px");
}

var directionPicker = new joint.ui.SelectBox({
    width: 150,
    options: [
        { value: 'L', content: 'Right-Left' },
        { value: 'R', content: 'Left-Right' },
        { value: 'T', content: 'Bottom-Top' },
        { value: 'B', content: 'Top-Bottom', selected: true },
    ]
}).on('option:select', function(option) {
    _.invoke(graph.getElements(), 'set', 'direction', option.value);
    graphLayout.layout();
    paperScroller.centerContent();
});

$('#orgchart-direction').append(directionPicker.render().el);

function addMessage(message) {
	
}