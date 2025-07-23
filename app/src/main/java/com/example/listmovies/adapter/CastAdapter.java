package com.example.listmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.listmovies.R;
import com.example.listmovies.api.CastMember;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private final Context context;
    private List<CastMember> castList = new ArrayList<>();
    private OnCastMemberClickListener listener; // --- 1. متغير لتخزين الـ Listener ---

    // --- 2. تعريف الـ Interface ---
    public interface OnCastMemberClickListener {
        void onCastMemberClick(int actorId);
    }

    public void setOnCastMemberClickListener(OnCastMemberClickListener listener) {
        this.listener = listener;
    }
    // --- نهاية الجزء الجديد ---

    public CastAdapter(Context context) {
        this.context = context;
    }

    public void setCastList(List<CastMember> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cast_member, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastViewHolder holder, int position) {
        CastMember castMember = castList.get(position);
        holder.actorName.setText(castMember.getName());
        holder.characterName.setText(castMember.getCharacter());

        String imageUrl = "https://image.tmdb.org/t/p/w185" + castMember.getProfilePath();
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_launcher_background).into(holder.actorImage);

        // --- 3. تفعيل الـ OnClickListener على العنصر ---
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                // عند الضغط، نقوم بإرسال الـ ID الخاص بالممثل إلى الـ Activity
                listener.onCastMemberClick(castMember.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        // لتجنب الأخطاء، تحقق دائمًا من أن القائمة ليست null
        return castList != null ? castList.size() : 0;
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView actorImage;
        TextView actorName;
        TextView characterName;

        public CastViewHolder(@NonNull View itemView) {
            super(itemView);
            actorImage = itemView.findViewById(R.id.actor_image);
            actorName = itemView.findViewById(R.id.actor_name);
            characterName = itemView.findViewById(R.id.character_name);
        }
    }
}