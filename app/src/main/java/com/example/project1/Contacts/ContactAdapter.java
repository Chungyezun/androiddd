package com.example.project1.Contacts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project1.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    public static final String EXTRA_MESSAGE = "CONTACT_DATA";
    private Set<Contact> mcontacts;
    public Set<Contact> getContactList(){
        return mcontacts;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView personName;
        public TextView phoneNumber;
        public ImageView personImg;
        // contact 에 필요한 세가지 정보를 준다.

        public ViewHolder(View itemView){ //Constructor for ViewHolder
            super(itemView);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            phoneNumber = (TextView) itemView.findViewById(R.id.person_number);
            personImg = (ImageView) itemView.findViewById(R.id.person_photo);
            // 인터페이스랑 대충 연결하기...
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // pos 정보를 새 activity 로 옮겨주자.
                        Log.d("RECYCLE:PRESSED","You have pressed - "+pos);
                        Intent intent = new Intent(view.getContext(), Contact_Details.class);
                        intent.putExtra(EXTRA_MESSAGE,pos);
                        view.getContext().startActivity(intent); // intent 를 통해 새 activity 에 접속
                    }
                    // activity 를 옮김과 동시에, 새 activity 에 변수를 넘기고 싶을 때!
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view){
                    //"EDIT MODE" 로 만들어 동시삭제 기능을 넣자.

                    return true;
                }
            });


        }





        //ViewHolder 에 press 기능 추가!!!!!!!!!!!!!!




    }
    public ContactAdapter(Set<Contact> contacts) {
        mcontacts = contacts;
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // LayoutInfaltor 는 XML을 View 로 변환시키는 역활을 한다..?

        // item_contact.xml 파일을 view로 변환시켰다. .inflate 함수를 통해.
        View contactView = inflater.inflate(R.layout.item_contact,parent,false);

        // 이렇게 변환한 View 를 ViewHolder 에 넣고,
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder; //리턴?
    }


    // 여기는 주어진 데이터를 통해서 본격적으로 변환하는 함수
    // viewHolder, position 이 주어지면,
    // viewHolder 에 적절한 데이터를 넣어 변환시킨다.
    @Override
    public void onBindViewHolder(ContactAdapter.ViewHolder viewHolder, int position){
        List<Contact> imsi = new ArrayList<>(mcontacts);
        Contact contact = imsi.get(position); // List 에서 가져오기
        TextView personName = viewHolder.personName;
        personName.setText(contact.getName());
        TextView personNumber = viewHolder.phoneNumber;
        personNumber.setText(contact.getNumber());

        ImageView personFace = viewHolder.personImg;
        personFace.setImageResource(R.drawable.ic_launcher_foreground); // 기본 이미지로 두자...
        //이미지는 어떻게 처리할까....
    }

    @Override
    public int getItemCount(){
        if(mcontacts == null){
            return 0;
        }else{
            return mcontacts.size();

        }
    }

}
