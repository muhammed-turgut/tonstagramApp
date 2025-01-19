import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.muhammedturgut.tonstagram.databinding.RecyclerRowBinding
import com.muhammedturgut.tonstagram.model.ProfilModel
import com.squareup.picasso.Picasso

class ProfilAdapter(val postProfilList:ArrayList<ProfilModel>):RecyclerView.Adapter<ProfilAdapter.ProfilPostHolder>(){
    class ProfilPostHolder(val binding:RecyclerRowBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfilPostHolder {
        val binding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProfilPostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postProfilList.size
    }

    override fun onBindViewHolder(holder: ProfilPostHolder, position: Int) {

        Picasso.get().load(postProfilList.get(position).dowlandUrl).into(holder.binding.imageView5)
    }

}