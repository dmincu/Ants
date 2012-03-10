import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MyBot implements Bot {
	
	List<Aim> directions = new ArrayList<Aim>(EnumSet.allOf(Aim.class));
	Set<Tile> foods;
	Set<Tile> movedAnts;
	Set<Tile> destinations;
	Map<Tile, Tile> previousFoodlessAnts;
	Map<Tile, Tile> actualFoodlessAnts;

	
	public static void main(String[] args) 
	{
		Ants.run(new MyBot());
	}
	
	public void do_turn(Ants ants)
	{
		/**
		 * Acestea trebuie reinitializate la fiecare tura.
		 */
		foods = new HashSet<Tile>();
		movedAnts = new HashSet<Tile>();
		destinations = new HashSet<Tile>();
		
		gatherFood(ants);
		scout(ants);
	}
	
	/**
	 * Se bazeaza pe algoritmul lui Lee.
	 * @param ants
	 */
	public void gatherFood(Ants ants)
	{	
		/** 
		 * Initializarea cu locatiile de mancare si sortarea acestora
		 * pentru a permite furnicilor care sunt apropiate de musuroi
		 * sa ramana in defensiva si sa colecteze mancarea, celelalte
		 * putand sa exploreze in continuare.
		 */
		foods = ants.food();
	
		/** 
		 * Pentru fiecare mancare, cautam radial prima furnica. 
		 */
		for (Tile food : foods)
		{
			/** 
			 * Coada de locatii. Cat timp coada nu este vida
			 * mai exista locatii de verificat pentru a gasi
			 * o furnica.
			 */
			Queue<Tile> trace = new LinkedList<Tile>();
			trace.add(food);
			
			/**
			 * Setul care contine locatiile puse deja in coada.
			 */
			Set<Tile> checkedTiles = new HashSet<Tile>();
			checkedTiles.add(food);
			
			out:
				while (!trace.isEmpty())
				{
					Tile location = trace.poll();
					for (Aim direction : directions)
					{
						Tile destination = ants.tile(location, direction);
						if (ants.ilk(destination).isAnt())
						{
							if (ants.ilk(location).isUnoccupied() && !destinations.contains(location))
							{
								ants.issueOrder(destination, findOposite(direction));
								movedAnts.add(destination);
								destinations.add(location);
								ants.ilk(location).setLv(ants.turn());
							}
							break out;
						}
						else
						{
							if (ants.ilk(destination).isPassable())
							{
								if (!checkedTiles.contains(destination))
								{
									trace.add(destination);
									checkedTiles.add(destination);
								}
							}
						}
					}
				}
		}
	}

	/**
	 * Functia de explorare.
	 * @param ants
	 */
	public void scout(Ants ants)
	{
		
		int dr = 0, dc = 0;
		
		/**
		 * Furnicile care au ramas le trimitem sa exploreze.
		 */	
		List<Tile> ffm=new ArrayList<Tile>();
				
		actualFoodlessAnts = new HashMap <Tile, Tile>();
		
		for (Tile location : ants.myAnts())
			if (!movedAnts.contains(location))
				ffm.add(location);
				
		/**
		 * Daca o furnica ce nu a gasit mancare a fost trimisa sa exploreze
		 * si in turul anterior, ea isi pastreaza directia anterioara
		 */	        
		maintainDestination(ants, actualFoodlessAnts, ffm);
		        
		int nrants = ffm.size(), i = 0;
		int dif = 0;
		 
		if (nrants > 0)
			dif = (int) (ants.rows() * 2 + ants.cols() * 2) / nrants;
		
		for (i = 0; i < nrants; i++)
		{
			int c = ffm.get(i).col(), cs = (int) (ants.cols() / 2) - 1;
			int r = ffm.get(i).row(), rs = (int) (ants.rows() / 2) - 1;
				
			if ((i * dif) < ants.cols()) 
			{ 
				dr = r - rs; 
				dc = c - cs + i * dif;
			}
			else 
				if ((i * dif) < (ants.cols() + ants.rows())) 
				{ 
					dc = c + cs; 
					dr = r - rs + i * dif - ants.cols(); 
				}
				else 
					if ((i * dif) < (2 * ants.cols() + ants.rows())) 
					{ 
						dr = r + rs;
						dc = c + cs - (i * dif - ants.cols() - ants.rows());
					}
					else 
					{ 
						dc = c - cs;
						dr = r + dr - (i * dif - 2 * ants.cols() - ants.rows());
					}	

			if (dr >= ants.rows()) 
				dr = dr - ants.rows();
			if (dr < 0) 
				dr = dr + ants.rows();
			if (dc >= ants.cols()) 
				dc = dc - ants.cols();
			if (dc < 0) 
				dc = dc + ants.cols();
				
			/**
			 * Adaugam furnica la actualFoodlessAnts cu coordonatele din turul urmator
			 * si destinatia pe care o ia acum. 
			 */
					
			Tile locate = ffm.get(i);
			Tile dest = new Tile(dr, dc);
				
			AStar(ants, dest, locate);
		}
		previousFoodlessAnts = actualFoodlessAnts;
	}
	
	public Tile getDest(Ants ants)
	{
		return new Tile( (int) (Math.random() * (ants.rows() - 1)), 
						 (int) (Math.random() * (ants.cols() - 1)));
	}
	
	private void maintainDestination (Ants ants, Map<Tile, Tile> actualFoodlessAnts, 
								List<Tile> ffm)
	{	
		for (int i = 0; i < ffm.size(); i++)
		{
			Tile location = ffm.get(i);
			Tile destination = previousFoodlessAnts.get(location);
			
			if (destination != null && !destination.equals(location))
			{
				ffm.remove(i);
				
				/** Aplicam AStar pt a afla coordonatele din turul urmator. **/
				AStar(ants, destination, location);
			}
		}			
	}

	
	protected Aim findOposite(Aim direction) 
	{
		if (direction.equals(Aim.NORTH))
			return Aim.SOUTH;
		else
			if (direction.equals(Aim.EAST))
				return Aim.WEST;
			else
				if (direction.equals(Aim.WEST))
					return Aim.EAST;
				else
					return Aim.NORTH;
	}
	
	public Tile findMinimum(List<Tile> lst, Tile t)
	{
		Collections.sort(lst, new Sort(t));
		return lst.get(0);
	}
	
	/**
	 * @param ants
	 * @param t1 Sursa.
	 * @param t2 Destinatia.
	 */
	protected void AStar(Ants ants, Tile t1, Tile t2)
	{
		List<Tile> openList = new ArrayList<Tile>();
		Set<Tile> checkedTiles = new HashSet<Tile>();
		
		BufferedWriter f = null;
		try
		{
			f = new BufferedWriter(new FileWriter("test.txt"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		/** Pasul 1 : Se adauga patratul de inceput in coada. **/
		openList.add(t1);
		checkedTiles.add(t1);

		out: while (!openList.isEmpty())
		{
			/** Pasul 2 : Se cauta patratul cu cel mai mic cost pana la destinatie 
			 * din openList. **/
			Tile t = findMinimum(openList, t2);
			openList.remove(t);
			
			/** Pasul 3 : Se adauga in lista checkedTiles. **/
			checkedTiles.add(t);
			try 
			{
				f.write(t.row()+" "+t.col()+"\r\n");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			/** Pasul 4 : Pentru patratele adiacente verificam daca au mai fost
			 * adaugate la checkedTiles sau daca sunt destinatia. **/
			for (Aim direction : directions)
			{
				Tile destination = ants.tile(t, direction);
				
				if (destination.equals(t2))
				{
					if (ants.ilk(t).isUnoccupied() && !destinations.contains(t))
					{
						ants.issueOrder(destination, findOposite(direction));
						movedAnts.add(destination);
						destinations.add(t);
						actualFoodlessAnts.put(ants.tile(destination, findOposite(direction)), t1);
						ants.ilk(t).setLv(ants.turn());
					}
					break out;
				}
				else
				{
					if (ants.ilk(destination).isPassable())
					{
						if (!checkedTiles.contains(destination))
						{
							openList.add(destination);
							checkedTiles.add(destination);
						}
					}
				}
			}
		}
	}
}

