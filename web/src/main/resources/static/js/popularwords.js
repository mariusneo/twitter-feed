$(document).ready(function (){

    var page_text='URL not provided or URL maybe content is behind https https https';
    var diameter = 600 - 30,
        limit=5000,
        format = d3.format(",d"),
        color = d3.scale.category20c();

    var bubble = d3.layout.pack()
        .sort(null)
        .size([diameter, diameter])
        .padding(1.5);

    var svg = d3.select("#svgid").append("svg")
        .attr("width", diameter)
        .attr("height", diameter)
        .attr("class", "bubble");


    $.ajax({
            url: serviceApiUrl + "/popularwords",
            dataType: 'jsonp',
            success: function(words2count) {
                build_bubbles(words2count);
            }
    });


    function build_bubbles(words2count){
        var wordList=[]; //each word one entry and contains the total count [ {cnt:30,title_list:[3,5,9],
        var wordCount=[];
        var wordMap={};
        var wordIdList=[];
        var wordTitleMap=[];
        var minVal=10000;
        var maxVal=-230;
        var regex=/\s|\.|,|;|[^a-zA-Z0-9]/g;
        var words;
        var wordId=0;
        var wordStr="";
        var titleID=0;

        var totalWords=words2count.length;
        for (var word in words2count){
            wordList.push(word);
            wordCount.push(words2count[word]);
            wordMap[word]=wordId;
            wordIdList.push(wordId);
            wordId++;
        }



        wordIdList.sort(function(x, y)
            {
                return -wordCount[x] + wordCount[y]
            }
        );

        var wordPercentStr="<p>Total words on the page="+totalWords+" , Words used in Visualization="+wordId+"</p>";
        wordPercentStr+="<div class=\"centered\"><table><tr><td>Word</td><td>Occurence/count</td><td>Good Density (%)</td><td>Gross Density (%)</td></tr>";
        var wi=0;
        var density;
        var grossDensity;
        for (var wp=0; wp<wordIdList.length;wp++)
        {
          wi=wordIdList[wp];
          density=" "+(wordCount[wi]*100/wordId);
          density=density.substr(0,6);
          grossDensity=(" "+(wordCount[wi]*100/totalWords)).substr(0,6);
          wordPercentStr+="<tr>";
          wordPercentStr+="<td>"+wordList[wi]+"</td><td>"+wordCount[wi]+"</td><td>"+density+"</td><td>"+grossDensity+"</td>";
          wordPercentStr+="</tr>";
        }
        wordPercentStr+="</table></div>";
        $("#topwords").html(wordPercentStr);
        $("#countbox").text(wordId);

        minVal=10000;
        maxVal=-100;
        for (var wi=0; wi<wordList.length; wi++)
        {
            if (minVal>wordCount[wi] ) minVal=wordCount[wi];
            if (maxVal<wordCount[wi] ) maxVal=wordCount[wi];

        }
        var data=[
        wordList,
        wordCount
        ];

        var dobj=[];

        for (var di=0;di<data[0].length;di++)
        {
          dobj.push({"key":di,"value":data[1][di]});
        }

        display_pack({children: dobj}, data);
    }

    function display_pack(root, data)
    {
      var node = svg.selectAll(".node")
          .data(bubble.nodes(root)
          .filter(function(d) { return !d.children; }))
        .enter().append("g")
          .attr("class", "node")
          .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
          .style("fill", function(d) { return color(data[0][d.key]); })
            .on("mouseover", function(d,i)
        {
            d3.select(this).style("fill", "gold");
            showToolTip(" "+data[0][i]+"<br>"+data[1][i]+" ",d.x+d3.mouse(this)[0]+50,d.y+d3.mouse(this)[1],true);
            //console.log(d3.mouse(this));
        })
        .on("mousemove", function(d,i)
        {

            tooltipDivID.css({top:d.y+d3.mouse(this)[1],left:d.x+d3.mouse(this)[0]+50});
            //showToolTip("<ul><li>"+data[0][i]+"<li>"+data[1][i]+"</ul>",d.x+d3.mouse(this)[0]+10,d.y+d3.mouse(this)[1]-10,true);
            //console.log(d3.mouse(this));
        })
        .on("mouseout", function()
        {
            d3.select(this).style("fill", function(d) { return color(data[0][d.key]); });
            showToolTip(" ",0,0,false);
        })
        ;

      /*node.append("title")
          .text(function(d) { return data[0][d.key] + ": " + format(d.value); });
    */
      node.append("circle")
          .attr("r", function(d) { return d.r; })
          ;
          //.style("fill", function(d) { return color(data[0][d.key]); });

      node.append("text")
          .attr("dy", ".3em")
          .style("text-anchor", "middle")
          .style("fill","black")
          .text(function(d) { return data[0][d.key].substring(0, d.r / 3); });
    }
    //);



    function showToolTip(pMessage,pX,pY,pShow)
    {
      if (typeof(tooltipDivID)=="undefined")
      {
                 tooltipDivID =$('<div id="messageToolTipDiv" style="position:absolute;display:block;z-index:10000;border:2px solid black;background-color:rgba(0,0,0,0.8);margin:auto;padding:3px 5px 3px 5px;color:white;font-size:12px;font-family:arial;border-radius: 5px;vertical-align: middle;text-align: center;min-width:50px;overflow:auto;"></div>');

            $('body').append(tooltipDivID);
      }
      if (!pShow) { tooltipDivID.hide(); return;}
      //MT.tooltipDivID.empty().append(pMessage);
      tooltipDivID.html(pMessage);
      tooltipDivID.css({top:pY,left:pX});
      tooltipDivID.show();
    }

}) //document ready