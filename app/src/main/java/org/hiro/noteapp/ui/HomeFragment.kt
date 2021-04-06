package org.hiro.noteapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.clans.fab.FloatingActionButton
import org.hiro.noteapp.R
import org.hiro.noteapp.adapter.MainListAdapter
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.database.model.Category
import org.hiro.noteapp.database.model.Item
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task
import org.hiro.noteapp.databinding.FragmentNoteListBinding
import org.hiro.noteapp.util.CategoryAddDialog
import org.hiro.noteapp.util.HawkUtils
import org.hiro.noteapp.util.hideKeyboard
import org.hiro.noteapp.viewmodel.HomeViewModel
import org.hiro.noteapp.viewmodel.factory.MainViewModelFactory


class HomeFragment : Fragment() {
    private val actNoteFr = HomeFragmentDirections.actionNoteListFragmentToNoteFragment()
    private val adapter: MainListAdapter by lazy {
        MainListAdapter {
            onItemClick(it)
        }
    }
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: FragmentNoteListBinding
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_note_list, container, false
        )
        binding.lifecycleOwner = this
        val db = MyDatabase.getInstance(requireContext())
        val catID = arguments?.getInt("CAT_ID")
        Log.d("CATEG", "onCreateView: $catID")
        viewModel = ViewModelProvider(this, MainViewModelFactory(db, catID))
            .get(HomeViewModel::class.java)
        initObservers()
        initListeners()
        attachItemTouchHelper()
        initNavView()
        binding.viewModel = viewModel
        binding.noteList.adapter = adapter
        binding.noteList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        return binding.root
    }
    
    private fun initListeners() {
        binding.noteChip.setOnCheckedChangeListener { _, isChecked ->
            val isTask = binding.taskChip.isChecked
            val from = "chipNOTE:$isChecked t:$isTask"
            HawkUtils.isNoteSelected = isChecked
            adapter.putList(viewModel.notes.value, viewModel.tasks.value, isChecked, isTask)
        }
        binding.taskChip.setOnCheckedChangeListener { _, isChecked ->
            val isNote = binding.noteChip.isChecked
            val from = "chipTASK:$isChecked n:$isNote"
            HawkUtils.isTaskSelected = isChecked
            adapter.putList(viewModel.notes.value, viewModel.tasks.value, isNote, isChecked)
        }
        
        binding.noteChip.isChecked = HawkUtils.isNoteSelected
        binding.taskChip.isChecked = HawkUtils.isTaskSelected
        
        binding.buttonBackArrow.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navView)
        }
    }
    
    private fun initNavView() {
        val v = binding.navView.getHeaderView(0)
        val subMenu: SubMenu = binding.navView.menu.addSubMenu("Categories")
        val addCat = { i: Category ->
            subMenu.add(1, i.id!!.toInt(), i.id!!.toInt(), i.name)
            hideKeyboard(requireActivity())
        }
        val list = viewModel.categoriesDB.getCategories()
        for (i in list) {
            addCat(i)
        }
        v.findViewById<FloatingActionButton>(R.id.catAddFAB).setOnClickListener {
            CategoryAddDialog(requireContext()) {
                val categ = Category(name = it)
                val id = viewModel.categoriesDB.insert(categ)
                categ.id = id
                addCat(categ)
            }
        }
        binding.navView.setNavigationItemSelectedListener {
            when (val id = it.itemId) {
                R.id.taskFragment -> findNavController().navigate(id)
                R.id.noteFragment -> {
                    actNoteFr.setKey(-1)
                    findNavController().navigate(actNoteFr)
                }
                R.id.noteListFragment -> {
                    if (viewModel.currentCategory != 0)
                        findNavController().navigate(id)
                }
                else -> {
                    if (viewModel.currentCategory != id) {
                        val b = Bundle()
                        b.putInt("CAT_ID", id)
                        findNavController().navigate(R.id.noteListFragment, b)
                        viewModel.currentCategory = id
                    }
                }
            }
            binding.drawerLayout.closeDrawer(binding.navView)
            true
        }
        
    }
    
    private fun attachItemTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView, vh: RecyclerView.ViewHolder, tg: RecyclerView.ViewHolder,
            ) = true
    
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.getItemByPos(viewHolder.adapterPosition).apply {
                    when (this) {
                        is Note -> viewModel.noteDB.delete(this)
                        is Task -> viewModel.taskDB.delete(this)
                    }
                }
            }
        }).attachToRecyclerView(binding.noteList)
    }
    
    private fun initObservers() {
        viewModel.notes.observe(viewLifecycleOwner) {
            Log.d("DBGG", "notes: ${it.joinToString { n -> "[${n.id} ${n.text}]" }}")
            adapter.putList(it,
                viewModel.tasks.value,
                HawkUtils.isNoteSelected,
                HawkUtils.isTaskSelected)
        }
        viewModel.tasks.observe(viewLifecycleOwner) {
            Log.d("DBGG",
                "tasks: ${it.joinToString { n -> "[${n.id} ${n.taskDesc} ${n.subTasks.size}]" }}")
            adapter.putList(viewModel.notes.value,
                it,
                HawkUtils.isNoteSelected,
                HawkUtils.isTaskSelected)
        }
        
        viewModel.onAddClickEvent.observe(viewLifecycleOwner, {
            if (it != 0) {
                when (it) {
                    1 -> {
                        actNoteFr.setKey(-1)
                        this.findNavController().navigate(actNoteFr)
                    }
                    2 -> this.findNavController().navigate(R.id.taskFragment)
                }
                viewModel.addCompleted()
            }
        })
    }
    
    private fun onItemClick(it: Item) {
        when (it) {
            is Note -> {
                actNoteFr.setKey(it.id!!)
                findNavController().navigate(actNoteFr)
            }
            is Task -> {
                val b = Bundle()
                b.putLong("KEY", it.id!!)
                findNavController().navigate(R.id.taskFragment, b)
            }
        }
    }
    
}