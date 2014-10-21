package net.CMS.tracer;

import java.util.Collections;

import net.CMS.tracer.Graph.Graph;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Sort;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	ShapeRenderer sr;
	Texture img;
	OrthographicCamera cam;
	BitmapFont free;
	static final int scale = 10;
	private Graph graph;
	private Vector2[] nodes;
	private Array<Array<Integer>> connectedGraphs;
	private Array<Integer> lConnectedGraph;
	@Override
	public void create () {
		cam = new OrthographicCamera();
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		FreeTypeFontGenerator fg = new FreeTypeFontGenerator(Gdx.files.internal("Instruction.ttf"));
		FreeTypeFontParameter fp = new FreeTypeFontParameter();
		fp.size = 19;
		free = fg.generateFont(fp);
		sr = new ShapeRenderer();
			generate(300, 200, 200);
	}
	int genL = 100;
	int genW=100;
	int genH=100;
@Override
public void resize(int width, int height) {
	cam.setToOrtho(false, width, height);
	batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	super.resize(width, height);
}
private enum renderMode{
	ALL,LARGETS;
}
private renderMode r = renderMode.ALL;
	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(Gdx.input.isKeyPressed(Keys.W))cam.translate(0, 5);
		if(Gdx.input.isKeyPressed(Keys.A))cam.translate(-5, 0);
		if(Gdx.input.isKeyPressed(Keys.S))cam.translate(0, -5);
		if(Gdx.input.isKeyPressed(Keys.D))cam.translate(5, 0);
		if(Gdx.input.isKeyPressed(Keys.Q))cam.zoom = Math.min(cam.zoom + 0.1f, 1.5f);
		if(Gdx.input.isKeyPressed(Keys.E))cam.zoom = Math.max(cam.zoom - 0.1f, 0.3f);
		if(Gdx.input.isKeyPressed(Keys.SPACE))generate(genL, genW, genH);
		if(Gdx.input.isKeyJustPressed(Keys.R))r = r == renderMode.ALL? renderMode.LARGETS: renderMode.ALL;
		if(Gdx.input.isKeyJustPressed(Keys.UP) && genL <= genW*genH - 10) genL += 10;
		if(Gdx.input.isKeyJustPressed(Keys.DOWN) && genL > 10) genL -= 10;
		if(Gdx.input.isKeyJustPressed(Keys.RIGHT)){
			genW += 10;
			genH += 10;
		}
		if(Gdx.input.isKeyJustPressed(Keys.LEFT) && genW*genH > genL + 10){
			genW -= 10;
			genH -= 10;
		}
		cam.update();
		sr.setProjectionMatrix(cam.combined);

		if(nodes == null) return;
		sr.begin(ShapeType.Filled);
		//sr.rect(0, 0, 10, 10);
		sr.end();
		switch(r){
		case ALL:
			drawAllGraphs();
			break;
		case LARGETS:
			drawLargest();
			break;
		default:
			break;
		
		}
		/*
		*/
		batch.begin();
		free.draw(batch, ("use WASD to navigate, use Q and E to zoom, use SHIFT to only show largest graph and generate new graph with SPACE"),10, Gdx.graphics.getHeight()-75);
		free.draw(batch, ("use R to cycle between views"),10, Gdx.graphics.getHeight()-75-21);
		free.draw(batch, ("genL: " + genL +" genS: " + genW),10, Gdx.graphics.getHeight()-75-21-21);
		batch.end();
	}
	public void drawAllGraphs(){
		if(connectedGraphs.size < 2){
			drawLargest();
			return;
		}
		sr.begin(ShapeType.Line);
		for(Array<Integer> i: connectedGraphs){
			for(int n : i)
				for(int j = 0; j < nodes.length; j++)
					if(graph.hasEdge(n, j)){
						sr.setColor(new Color(i.hashCode()));
						sr.line(nodes[n].cpy().scl(scale), nodes[j].cpy().scl(scale));
						sr.setColor(Color.WHITE);
						sr.circle(nodes[n].x * scale, nodes[n].y* scale, 0.1f * scale);
						sr.circle(nodes[n].x * scale, nodes[n].y* scale, 0.2f * scale);

					}

		}
		sr.end();
	}
	public void drawLargest(){
		sr.begin(ShapeType.Line);
			for(int n : lConnectedGraph)
			{
				boolean ac [] = new boolean[4];
				for(int j = 0; j < nodes.length; j++)
					if(graph.getEdge(n, j) != 0){
						sr.setColor(new Color(lConnectedGraph.hashCode()));
						sr.line(nodes[n].cpy().scl(scale), nodes[j].cpy().scl(scale));
						ac[getEdgeDir(n, j)] = true;
						if(getEdgeDir(n, j) == 0) {	
							sr.line(nodes[j].cpy().add(0, 1).scl(scale), nodes[j].cpy().add(0.5f, 0.5f).scl(scale));
							sr.line(nodes[j].cpy().add(0, 1).scl(scale), nodes[j].cpy().add(-0.5f, 0.5f).scl(scale));
							
						}
						if(getEdgeDir(n, j) == 1){
							sr.line(nodes[j].cpy().add(0, -1).scl(scale), nodes[j].cpy().add(0.5f, -0.5f).scl(scale));
							sr.line(nodes[j].cpy().add(0, -1).scl(scale), nodes[j].cpy().add(-0.5f, -0.5f).scl(scale));
						}
						if(getEdgeDir(n, j) == 2){
							sr.line(nodes[j].cpy().add(-1, 0).scl(scale), nodes[j].cpy().add(-0.5f, 0.5f).scl(scale));
							sr.line(nodes[j].cpy().add(-1, 0).scl(scale), nodes[j].cpy().add(-0.5f, -0.5f).scl(scale));
						}
						if(getEdgeDir(n, j) == 3){
							sr.line(nodes[j].cpy().add(1, 0).scl(scale), nodes[j].cpy().add(0.5f, 0.5f).scl(scale));
							sr.line(nodes[j].cpy().add(1, 0).scl(scale), nodes[j].cpy().add(0.5f, -0.5f).scl(scale));
						}
						sr.setColor(Color.WHITE);
						sr.circle(nodes[n].x * scale, nodes[n].y* scale, 0.4f);

					}
				sr.setColor(Color.WHITE);
				if(!ac[0])sr.line(nodes[n].cpy().scl(scale), nodes[n].cpy().add(0,-0.5f).scl(scale));
				if(!ac[1])sr.line(nodes[n].cpy().scl(scale), nodes[n].cpy().add(0,0.5f).scl(scale));
				if(!ac[2])sr.line(nodes[n].cpy().scl(scale), nodes[n].cpy().add(0.5f,0).scl(scale));
				if(!ac[3])sr.line(nodes[n].cpy().scl(scale), nodes[n].cpy().add(-0.5f,0).scl(scale));
			}

		sr.end();
	}
	public int getEdgeDir(int n1, int n2){
		if(nodes[n1].x == nodes[n2].x && nodes[n1].y > nodes[n2].y) return 0;	//-90
		if(nodes[n1].x == nodes[n2].x && nodes[n1].y < nodes[n2].y) return 1;	//90
		if(nodes[n1].x < nodes[n2].x && nodes[n1].y == nodes[n2].y) return 2;	// 0
		if(nodes[n1].x > nodes[n2].x && nodes[n1].y == nodes[n2].y) return 3;	//180
		return -1;
	}
	public void generate(int length, int width, int height){
		nodes = new Vector2[length];
		graph = new Graph(length);
		connectedGraphs = new Array<Array<Integer>>();
		boolean[][] taken = new boolean[width+1][height+1];
		for(int i = 0; i < length; i++){
			while(true){
				int x =  (int)MathUtils.random(0, width);
				int y =  (int)MathUtils.random(0,height);
				if(!taken[x][y]){
					graph.addNode(i);
					nodes[i] = new Vector2(x, y);
					taken[x][y] = true;
					System.out.println(x + " " + y + " " + i);
					break;
				}
			}
		}
		for(int i = 0; i < nodes.length; i++)
			for(int j = 0; j < nodes.length; j++)
				if( i != j){
					int x1,y1,x2,y2;
					x1 = (int) nodes[i].x;
					y1 = (int) nodes[i].y;
					x2 = (int) nodes[j].x;
					y2 = (int) nodes[j].y;
					if((x1 == x2 || y1 == y2) && !graph.hasEdge(i, j)){
						graph.addEdge(i, j, 1);
					}
				}
		connectedGraphs = graph.getConnectedGraphcs();		
		int s= 0;
		for(Array<Integer> i: connectedGraphs){
			if(i.size > s){
				s = i.size;
				lConnectedGraph = i;
			}
		}
		Sort.instance().sort(lConnectedGraph);
	}

}
