package mygame;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;

public class Game extends AbstractAppState implements ActionListener {

		Main main;
		
		
		@Override
		public void initialize(AppStateManager stateManager, Application app)
		{
			 this.main = (Main)app;
			 main.mainWall = new Wall(4, main);
       main.getRootNode().attachChild(main.mainWall);
		}
		
		@Override
		public void update(float tpf)
		{
				//update the skeleton
				int[][] joints;
				if(main.mocapPlayer){
						joints = main.player.getJoints();
				}else{
						joints = main.mocap.getJoints();
				}
				if (joints != null) {

						
						main.player1.setJoints((main.mocapPlayer)? main.player.getJoints(): main.mocap.getJoints() );
						main.player1.draw();
				}
		}
		
		@Override
		public void cleanup()
		{
				
		}
		
		
		public void onAction(String name, boolean isPressed, float tpf) {
				
		}
		
		
}
