import java.util.Comparator;


public class Sort implements Comparator<Tile> {
	
	Tile reference;
	
	Sort() {}
	
	Sort(Tile reference)
	{
		this.reference = reference;
	}
	
	@Override
	public int compare(Tile arg0, Tile arg1) 
	{
		/** Folosim distanta Manhattan **/
		int x = 10*(Math.abs(arg0.col() - reference.col()) + 
					Math.abs(arg0.row() - reference.row()));
		int y = 10*(Math.abs(arg1.col() - reference.col()) + 
					Math.abs(arg1.row() - reference.row()));
		
		return x - y;
	}

}
