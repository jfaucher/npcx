package net.gamerservices.npcx;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.gamerservices.npclibfork.BasicHumanNpc;
import net.gamerservices.npclibfork.NpcSpawner;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Account;

public class myNPC {
	public npcx parent;
	public String name = "dummy";
	public String id = "0";
	String category = "container";
	public BasicHumanNpc npc;
	public myFaction faction;
	public mySpawngroup spawngroup;
	public myLoottable loottable;
	public List< myShopItem > shop = new CopyOnWriteArrayList< myShopItem >();
	public double coin = 250000;
	public HashMap<String, myTriggerword> triggerwords = new HashMap<String, myTriggerword>();
	public int chest = 0;
	public int legs = 0;
	public int helmet = 0;
	public int weapon = 0;
	public int boots = 0;
	public List< myHint > hints = new CopyOnWriteArrayList< myHint >();
	public myPathgroup pathgroup;
	public int currentpathspot = 0;
	public int movecountdown = 0;
	public boolean moveforward = true;
			
	myNPC(npcx parent, HashMap<String, myTriggerword> triggerwords, Location location, String name)
	{
		this.name = name;
		
		this.parent = parent;
		
		this.triggerwords = triggerwords;
		
	}
	
	public void onPlayerAggroChange(myPlayer myplayer)
	{
		int count = 0;
		
		// do i already ahve aggro?
		for (myTriggerword tw : triggerwords.values())
			{
				
				if (tw.word.toLowerCase().contains("attack"))
				{
					String send = variablise(tw.response,myplayer.player);
					say(myplayer, send);
					count++;
					return;
				}
				
				
		
		}
	}
	
	
	
	private void say(myPlayer myplayer, String string) {
		// TODO Auto-generated method stub
		myplayer.player.sendMessage(npc.getName()+" says to you, '"+string+"'");
	}

	public void onPlayerChat(myPlayer myplayer, String message, String category)
	{
		if (category.matches("shop"))
		{
			parseShop(myplayer, "Hello!");
		
		}
			
		int count = 0;
		int size = 0;
		//myplayer.player.sendMessage("Parsing:" + message + ":" + Integer.toString(this.triggerwords.size()));
		String message2=message+" ";
		for (String word : message2.split(" "))
		{
						
			if(count == 0)
			{
				for (myTriggerword tw : triggerwords.values())
				{
					//myplayer.player.sendMessage("Test:" + word + ":"+ tw.word);
					if (word.toLowerCase().contains(tw.word.toLowerCase()))
					{
						String npcattack = "NPCATTACK";
						String summonplayer = "NPCSUMMONPLAYER";
						String npcsummonzombie = "NPCSUMMONZOMBIE";
						
						
						// NPCATTACK variable
						if (tw.response.toLowerCase().contains(npcattack.toLowerCase()))
						{
								say(myplayer,"You will regret that");
								npc.aggro = myplayer.player;
								npc.follow = myplayer.player;
								return;

						}

						// NPCSUMMONPLAYER
						if (tw.response.toLowerCase().contains(summonplayer.toLowerCase()))
						{
								say(myplayer,"Come here");
								double x = npc.getBukkitEntity().getLocation().getX();
								double y = npc.getBukkitEntity().getLocation().getY();
								double z = npc.getBukkitEntity().getLocation().getZ();
								Location location = new Location(npc.getBukkitEntity().getLocation().getWorld(), x, y, z);
								
								myplayer.player.teleportTo(location);
								return;

						}

						// NPCSUMMONMOB
						if (tw.response.toLowerCase().contains(npcsummonzombie.toLowerCase()))
						{
							double x = myplayer.player.getLocation().getX();
							double y = myplayer.player.getLocation().getY();
							double z = myplayer.player.getLocation().getZ();
							Location location = new Location(npc.getBukkitEntity().getLocation().getWorld(), x, y, z);
							
							
							npc.getBukkitEntity().getWorld().spawnCreature(location,CreatureType.ZOMBIE);
							
							say(myplayer,"Look out");
							return;
						}

						
						String send = variablise(tw.response,myplayer.player);
						say(myplayer,send);
						count++;
						return;
	
					}
					
					
					size++;
				}
			}
		}
		if (count == 0 && size == 0)
		{
			say(myplayer,"I'm sorry. I'm rather busy right now.");
		} else {
				int count2 = 0;
				for (myTriggerword tw : triggerwords.values())
				{
					//myplayer.player.sendMessage("Test:" + word + ":"+ tw.word);
					if (tw.word.toLowerCase().contains("default") && size > 0 && count == 0)
					{
						String send = variablise(tw.response,myplayer.player);
						
						say(myplayer,send);
						count2++;
						return;
					}
					
					
				}
				if (count2 == 0)
				{
					say(myplayer,"I'm sorry. I'm rather busy right now.");
				}
				
		}
		
	}



	private String variablise(String response, Player player) {
		// TODO Auto-generated method stub
		String newresponse = response;
		if (response.contains("bankbalance"))
		{
			//System.out.println("Replacing bankbalance variable");
			Account account = iConomy.getBank().getAccount(player.getName());
			newresponse = response.replaceAll("bankbalance", Float.toString((float)account.getBalance()));
		}
		
		if (response.contains("playerbalance"))
		{
			//System.out.println("Replacing bankbalance variable");
			Account account = iConomy.getBank().getAccount(player.getName());
			newresponse = response.replaceAll("playerbalance", Float.toString((float)account.getBalance()));
		}
		
		if (response.contains("playerhealth"))
		{
			//System.out.println("Replacing bankbalance variable");
			newresponse = response.replaceAll("playerhealth", Integer.toString(player.getHealth()));
		}
		
		if (response.contains("playername"))
		{
			//System.out.println("Replacing bankbalance variable");
			Account account = iConomy.getBank().getAccount(player.getName());
			newresponse = response.replaceAll("playername", player.getName());
		}

		
		return newresponse;
	}
	
	public double checkHints(int id)
	{
		for (myHint hint : hints)
		{
			if (hint.id == id)
			{
				hint.age++;
				return (float)hint.price;
				
			}
		}
		// return basic price
		return 1;
		
	}



	public void parseShop(myPlayer player, String message) 
	{
		// TODO Auto-generated method stub
		//myplayer.player.sendMessage("Parsing:" + message + ":" + Integer.toString(this.triggerwords.size()));
		String message2=message+" ";
		String[] aMsg = message.split(" ");
		int size = aMsg.length;
		//player.player.sendMessage("DEBUG: " + size);
		if (aMsg[0].toLowerCase().matches("help"))
		{
						
			say(player,"What do you need? [list], [sell] or [buy]");
			return;
		}
		
		if (aMsg[0].toLowerCase().matches("list"))
		{
			boolean match = false;
			
			
			if (size == 2)
 		    {
				match = true;
 		    }
				
			int count = 0;
			for (myShopItem item : shop)
			{
				count++;
				if (match == true)
				{
					if (item.item.getType().name().contains(aMsg[1]))
					{
						say(player,item.item.getType().name() + " x " + item.item.getAmount() + " selling at " + (float)checkHints(item.item.getTypeId()) + " before commision");
					}
				} else {
					say(player,item.item.getType().name() + " x " + item.item.getAmount() + " selling at " + (float)checkHints(item.item.getTypeId()) + " before commision");
				}
			}
			say(player,count + " items in the shop.'");
			
			return;

			
		}
		if (aMsg[0].toLowerCase().matches("sell"))
		{
			if (aMsg.length < 3)
			{		
				say(player,"sell [itemid] [amount]");
				return;
			} else {
				
				myShopItem shopitem = new myShopItem();
				ItemStack item = new ItemStack(0);
				shopitem.item = item;
				// todo price
				
				
				try 
				{
					item.setTypeId(Material.matchMaterial(aMsg[1]).getId());
				} catch (NullPointerException e)
				{
					// lol
					this.parent.sendPlayerItemList(player.player);
					say(player,"Hmm try another item similar named to "+aMsg[1]+" and i might be interested.");
					//e.printStackTrace();
					return;
				
				} catch (Exception e)
				{
					this.parent.sendPlayerItemList(player.player);
					say(player,"Hmm try another item similar named to "+aMsg[1]+" and i might be interested.");
					//e.printStackTrace();
					return;
					
				}
				item.setAmount(Integer.parseInt(aMsg[2]));
				int count = 0;
				for (ItemStack curitem : player.player.getInventory().getContents())
				{
					if (curitem.getTypeId() == item.getTypeId())
					{
						count = count + curitem.getAmount();
						//player.player.sendMessage(npc.getName() + " says to you, '"+ curitem.getTypeId() +"/"+curitem.getAmount() +"'");
					}
					
					
				}
				
				if (count >= item.getAmount())
				{
					
					say(player,"Ok thats "+ item.getAmount() +" out of your "+count +".");
					double totalcoins = 0;
					totalcoins = (float)(item.getAmount() * checkHints(shopitem.item.getTypeId()) * 0.80);
					
					if (this.coin >= totalcoins)
					{
						player.player.getInventory().removeItem(item);
						shop.add(shopitem);
						say(player,"Thanks! Heres your " + (float)totalcoins + " coins.");
						Account account = iConomy.getBank().getAccount(player.name);
						this.coin = (float)this.coin - (float)totalcoins;
						account.add(totalcoins);
					} else {
						say(player,"Sorry, I only have: "+(float)this.coin+" and thats worth "+(float)totalcoins+"!");
					}
				} else {
					
					say(player,"Sorry, you only have: "+count+" !");
				}
			}
			return;
		}
		
		
		if (aMsg[0].toLowerCase().matches("buy"))
		{
			if (aMsg.length < 3)
			{
				say(player,"buy [itemid] [amount]");
				return;
			} else {
				if (Integer.parseInt(aMsg[2]) > 0)
				{
					int amount = Integer.parseInt(aMsg[2]);
					int originalamount = Integer.parseInt(aMsg[2]);
					int found = 0;
					double totalcost = 0;
					List< myShopItem > basket = new CopyOnWriteArrayList< myShopItem >();
					if (shop.size() > 0)
					{
						
						for (myShopItem item : shop)
						{
							try 
							{
							
								if (item.item.getTypeId() == Material.matchMaterial(aMsg[1]).getId())
								{
									
									//player.player.sendMessage(npc.getName() + " says to you, 'Hmm: " + item.item.getTypeId() + " is worth "+ (item.price+item.price*0.10) +" coin each'");
									
										found++;
										
										
										double cost = ((checkHints(item.item.getTypeId()) * 1.10) * item.item.getAmount());
										if (cost > 0)
										{
											if (amount != 0)
											{
											
												if (item.item.getAmount() <= amount)
												{
													this.coin = (float)this.coin + (float)cost;
													totalcost = (float)totalcost + (float)cost;
													amount = amount - item.item.getAmount();
													
													shop.remove(item);
													basket.add(item);
													
													say(player,(float)cost + " coins for this stack.");
												} else {
													this.coin = (float)this.coin + (float)cost;
													totalcost = (float)totalcost + (((float)cost/item.item.getAmount())*amount);
													
													myShopItem i = new myShopItem();
													i.price = (((float)cost/item.item.getAmount())*amount);
													ItemStack is = new ItemStack(item.item.getType());
													i.item = is;
													is.setAmount(amount);
													is.setTypeId(item.item.getTypeId());
													
													amount = 0;
													basket.add(i);
													item.item.setAmount(item.item.getAmount()-i.item.getAmount());
													say(player,(float)cost + " coins for this stack.");
													
													
												}
											}
										}
										
									
								} else {
									// ignore this type
									
								}
							
							} catch (NullPointerException e)
							{
								this.parent.sendPlayerItemList(player.player);
								say(player,"Hmm try another item similar named to "+aMsg[1]+" and i might be interested");
								return;
							}
						}
						
						if (found < 1)
						{
							// nothing was ever placed in a basketm, can return
							say(player,"Sorry, out of stock in that item.");
							return;
						}
						
						if (totalcost > 0)
						{
							Account account = iConomy.getBank().getAccount(player.name);
							
							if (account.hasEnough((float)totalcost))
							{
								for (myShopItem i : basket)
								{
									player.player.getInventory().addItem(i.item);
									basket.remove(i);
								}
								
								say(player,"Thanks, " + (float)totalcost + " coins.");
								
								account.subtract((float)totalcost);
								double each = totalcost / originalamount;
															
								// update hints
								updateHints(Material.matchMaterial(aMsg[1]).getId(),each);
								return;
							} else {
								for (myShopItem i : basket)
								{
									shop.add(i);
									basket.remove(i);
								}
								say(player,"Sorry, you don't have enough (" + (float)totalcost + " coins).");
								return;

							}
						}
						
					} else {
						say(player," says to you, 'Sorry, totally out of stock!");
						return;
					}
				
				}
			}
		}
		
		// Unknown command
		onPlayerChat(player,message,"shop");
		say(player,"Sorry, can i [help] you?");
		
		return;
			
	}



	private void updateHints(int parseInt, double each) {
		// TODO Auto-generated method stub
		
		// is it old?
		int age = 0;
		
		for (myHint hint : this.hints)
		{
			if (hint.id == parseInt)
			{
				age = hint.age;
				hints.remove(hint);
			}
		}
		
			myHint h = new myHint();
			h.id = parseInt;
			h.price = (float)each;
			h.age = 0;
			this.hints.add(h);
	}

	public BasicHumanNpc Spawn(String id2, String name2,
			World world, double x, double y, double z, Double yaw, Double pitch) {
		// TODO Auto-generated method stub
		BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(this,id2, name2, world, x, y, z,yaw , pitch);
		this.npc = hnpc;
        
		return hnpc;
	}

	public void Delete() {
		// TODO Auto-generated method stub
		
	}

	public void onRightClick(Player p) {
		// TODO Auto-generated method stub
		for (myPlayer player : parent.universe.players.values()){
			if (player.player == p)
			{
				if (player.target != null)
				{
                    p.sendMessage("* Target cleared!");
                    player.target = null;
					
				} else {
					player.target = this.npc;
					
					int tNPCID = 0;
					int tGPID = 0;
					int tFID = 0;
					int tPGID = 0;
					int tLTID = 0;
					
					if (this.parent != null)
					{
						tNPCID = Integer.parseInt(this.id);
						
    					if (this.spawngroup != null)
    						tGPID = this.spawngroup.id;
    					if (this.faction != null)
    						tFID = this.faction.id;
    					if (this.pathgroup != null)
    						tPGID = this.pathgroup.id;
    					if (this.loottable != null)
    						tLTID = this.loottable.id;
					}
					
					p.sendMessage("**************************************************************");
					p.sendMessage("NPCID ("+tNPCID+"):SG ("+tGPID+"):F ("+tFID+"):PG ("+tPGID+"):L ("+tLTID+")");
                    p.sendMessage("* You are now chatting to: " + name + ". Right Click to cancel.");
                    p.sendMessage("* Words in [brackets] you should type! Type 'hello' to begin.");
                    p.sendMessage("**************************************************************");
                    if (player.target != null && player.target.parent != null && player.target.parent.category != null)
                    {
                        if (player.target.parent.category.matches("shop"))
    					{
                            onPlayerChat(player, "Hello!","shop");
    	
                        } else {
                        	onPlayerChat(player, "Hello!","");
                        }
                    } else {
                    	onPlayerChat(player, "Hello!","");
                    }

                    
				}
				
			} else {
				if (player.name == p.getName())
				{
					p.sendMessage("Your name is right but your player is wrong");
					
				}
				
			}
		}
		this.npc.forceMove(this.npc.getFaceLocationFromMe(p.getLocation(),true));

	}

	public void onClosestPlayer(Player p) {
		// TODO Auto-generated method stub
		
		int count2 = 0;
		for (myTriggerword tw : triggerwords.values())
		{
			//myplayer.player.sendMessage("Test:" + word + ":"+ tw.word);
			if (tw.word.toLowerCase().contains("EVENT_CLOSEST"))
			{
				String send = variablise(tw.response,p);
				
				for (myPlayer player : this.parent.universe.players.values())
				{
					if (p == player.player)
					{
						say(player,send + "'");
					}
				}
				
				count2++;
				
			}
		}
		if (count2 == 0)
		{
			// If i dont have a triggerword, dont respond
			//p.sendMessage(npc.getName() + " says to you, 'Watch your back.'");
		}
		
		// face target
		//this.npc.forceMove(this.npc.getFaceLocationFromMe(p.getLocation(),true));
	}

	public void onBounce(Player p) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		
		int count2 = 0;
		for (myTriggerword tw : triggerwords.values())
		{
			//myplayer.player.sendMessage("Test:" + word + ":"+ tw.word);
			if (tw.word.toLowerCase().contains("EVENT_BOUNCE"))
			{
				String send = variablise(tw.response,p);
				
				for (myPlayer player : this.parent.universe.players.values())
				{
					if (p == player.player)
					{
						say(player,send + "'");
					}
				}
				
				count2++;
				
			}
		}
		if (count2 == 0)
		{
			// If i dont have a triggerword, dont respond
			p.sendMessage(npc.getName() + " says to you, 'Hey! Watch where you are going!'");
		}
		
		this.npc.forceMove(this.npc.getFaceLocationFromMe(p.getLocation(),true));
	}

	public void onDeath(LivingEntity p) {
		// TODO Auto-generated method stub
		if (p instanceof Player)
		{
			int count2 = 0;
			for (myTriggerword tw : triggerwords.values())
			{
				//myplayer.player.sendMessage("Test:" + word + ":"+ tw.word);
				if (tw.word.toLowerCase().contains("EVENT_DEATH"))
				{
					String send = variablise(tw.response,(Player)p);
					
					for (myPlayer player : this.parent.universe.players.values())
					{
						if (p == player.player)
						{
							say(player,send + "'");
						}
					}
					count2++;
				}
			}
			if (count2 == 0)
			{
				// If i dont have a triggerword, respond with
				((Player) p).sendMessage(npc.getName() + " says to you, 'I will be avenged for this!'");
			}
			
			((Player) p).sendMessage("You have slain " + this.name + "!");
			if (this.faction != null)
			{
				((Player) p).sendMessage("Your standing with " + this.faction.name + " has gotten worse!");
			}
			
		}

	}
}
