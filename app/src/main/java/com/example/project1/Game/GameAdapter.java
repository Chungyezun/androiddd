package com.example.project1.Game;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder>{
    private List<Player> mPlayers;
    public List<Player> getPlayerList(){
        return mPlayers;
    }
    private String unique1;


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView playername;
        public TextView unique;
        public TextView hp;
        public TextView max_hp;

        public ViewHolder(View itemView){ //Constructor for ViewHolder
            super(itemView);
            playername = (TextView) itemView.findViewById(R.id.player_name);
            unique = (TextView) itemView.findViewById(R.id.person_class);
            max_hp = (TextView) itemView.findViewById(R.id.MAX_HP);
            hp = (TextView) itemView.findViewById(R.id.hp);
            // 인터페이스랑 대충 연결하기...
//            itemView.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View view){
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION) {
//                        // pos 정보를 새 activity 로 옮겨주자.
//                        Log.d("RECYCLE:PRESSED","You have pressed - "+pos);
//                        Intent intent = new Intent(view.getContext(), Contact_Details.class);
//                        intent.putExtra(EXTRA_MESSAGE,pos);
//                        view.getContext().startActivity(intent); // intent 를 통해 새 activity 에 접속
//                    }
//                    // activity 를 옮김과 동시에, 새 activity 에 변수를 넘기고 싶을 때!
//                }
//            });
//            itemView.setOnLongClickListener(new View.OnLongClickListener(){
//                @Override
//                public boolean onLongClick(View view){
//                    //"EDIT MODE" 로 만들어 동시삭제 기능을 넣자.
//
//                    return true;
//                }
//            });


        }





        //ViewHolder 에 press 기능 추가!!!!!!!!!!!!!!




    }
    public GameAdapter(List<Player> players) {

        mPlayers = players;
    }

    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // LayoutInfaltor 는 XML을 View 로 변환시키는 역활을 한다..?

        // item_player.xml 파일을 view로 변환시켰다. .inflate 함수를 통해.
        View playerView = inflater.inflate(R.layout.item_player,parent,false);

        // 이렇게 변환한 View 를 ViewHolder 에 넣고,
        GameAdapter.ViewHolder viewHolder = new GameAdapter.ViewHolder(playerView);
        return viewHolder; //리턴?
    }


    // 여기는 주어진 데이터를 통해서 본격적으로 변환하는 함수
    // viewHolder, position 이 주어지면,
    // viewHolder 에 적절한 데이터를 넣어 변환시킨다.
    @Override
    public void onBindViewHolder(GameAdapter.ViewHolder viewHolder, int position){
        Player player = mPlayers.get(position); // List 에서 가져오기
        TextView playername = viewHolder.playername;
        playername.setText(player.getName());
        TextView unique = viewHolder.unique;
        unique.setText(player.getUnique());
        TextView hp = viewHolder.hp;
        TextView max_hp = viewHolder.max_hp;
        hp.setText(String.valueOf(player.getHP()));
        max_hp.setText(String.valueOf(player.getMAXhp()));

    }

    @Override
    public int getItemCount(){
        if(mPlayers == null){
            return 0;
        }else {
            return mPlayers.size();
        }
    }

}

