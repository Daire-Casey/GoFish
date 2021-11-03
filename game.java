import java.util.*;

public class game {
	
	// the main method is where the game takes place
	public static void main(String [] args) {
		Scanner sc = new Scanner(System.in);
		boolean replay = false; // for when the player is asked if they want another game
		int [] opponentMemory = new int [3]; // ranks recently asked by the opponent are stored here
		int opponentMemoryIndex = 0;
		System.out.println("Welcome to Go Fish!");
		
		do { // while the player wants another game
			LinkedList deck = shuffle(); // a new deck is created and shuffled
			LinkedList pHand = new LinkedList(); // the player's hand
			LinkedList oHand = new LinkedList(); // the computer opponent's hand
			int pScore = 0, oScore = 0; // the scores, go up when a book is collected
			boolean anotherRound = true; // for whether to continue on to the next turn or whether the game has ended
			boolean extraTurn; // for if an extra turn is awarded
			replay = false;
			
			
			for(int i = 0; i < 7; i++) { // this is the deal
				pHand.insertCard(deck.drawCard()); // cards are popped from the deck stack and inserted into the ordered linked list hands
				oHand.insertCard(deck.drawCard()); // alternating hands for each new card dealt
			}
			
			do { // while the game is not ready to be over
				System.out.println();
				System.out.println("~~~~~ IT IS YOUR TURN ~~~~~");
				do { // while the player receives extra turns
					extraTurn = false;
					System.out.println();
					pHand.printHand(); // the player's hand is shown to them
					int count = 0; // stores the number of cards received from the opponent in a turn
					int input; 
					boolean reenter = false; // for if the player enters something invalid
					int [] countArray = pHand.countHand(); // so we can check if the player has the card
					System.out.println("You: 'Do you have any ____ ?'");
					do { // while the player has not yet entered a valid rank
						reenter = false;
						input = sc.nextInt(); // player enters the rank they want from the opponent
						if(input < 2 || input > 10) { // reminds the player of the ranks that exist
							System.out.println(input + " is not a valid card rank. Please ask for a card rank between 2 and 10.");
							reenter = true;
						} else if(countArray[input - 2] == 0) { // if the player doesn't have that card
							System.out.println("Remember, you must have a " + input + " to ask for " + input + "s. Try asking for a different rank.");
							reenter = true;
						}
					} while(reenter); // lets the player try entering again
					Link check = new Link(0); // stores the taken card, or null if no card was taken
					for(int i = 0; i < 3; i++) { // opponent could have as many as 3 so we check thrice
						check = oHand.delete(input); // takes card from opponent
						if(check != null) count++; // records number of cards taken
						pHand.insertCard(check); // inserts card into players hand
					}
					
					if(count == 0) { // if the opponent had none of that rank
						System.out.println("Opponent: 'Go Fish!'");
						check = deck.drawCard(); // check is reused for the card drawn from the deck so we can check for a lucky catch
						System.out.println("You have drawn one " + check.data + " from the deck.");
						pHand.insertCard(check); // inserts the new card into the player's hand
						
						int bookCheck= pHand.checkForBook(); // checks if a book has been collected
						if(bookCheck != 0) {
							System.out.println("You have completed the book of " + bookCheck + "s!");
							pScore += 1; // increments player's score
						}
						
						if(check.data == input) { // if the player had to go fish but received their desired card from the deck
							extraTurn = true;
							System.out.println("A lucky catch! You drew exactly what you wanted, so you may take an extra turn.");
						}
					} else { // if the player took 1-3 cards from the opponent
						extraTurn = true;
						System.out.println("It's a catch! You have taken " + count + " card(s) of rank " + input + " from your opponent.");
						
						int bookCheck= pHand.checkForBook(); // checks if a book has been collected
						if(bookCheck != 0) {
							System.out.println("You have completed the book of " + bookCheck + "s!");
							pScore += 1;  // increments player's score
						}
						System.out.println("You may take an extra turn.");
					}
					
					if(oHand.isEmpty()) { // if the hand is empty new cards can be drawn
						int noOfNewCards = oHand.drawNewHand(deck);
						if(noOfNewCards != 0) {
							System.out.println("Opponent has emptied their hand and have drawn " + noOfNewCards + " new cards from the deck.");
							
							int bookCheck = oHand.checkForBook(); // checks if a book has been collected
							if(bookCheck != 0) {
								System.out.println("Opponent has completed the book of " + bookCheck + "s!");
								oScore += 1; // increments opponent's score
							}
						} else System.out.println("Opponent has emptied their hand.");  // if the deck was empty and 0 new cards were drawn
					}
					if(pHand.isEmpty()) { // if the hand is empty new cards can be drawn
						int noOfNewCards = pHand.drawNewHand(deck);
						if(noOfNewCards != 0) {
							System.out.println("You have emptied your hand and have drawn " + noOfNewCards + " new cards from the deck.");
							
							int bookCheck= pHand.checkForBook(); // checks if a book has been collected
							if(bookCheck != 0) {
								System.out.println("You have completed the book of " + bookCheck + "s!");
								pScore += 1;  // increments player's score
							}
						} else System.out.println("You have emptied your hand.");  // if the deck was empty and 0 new cards were drawn
					}
					
					if((pHand.isEmpty() && deck.isEmpty()) || (oHand.isEmpty() && deck.isEmpty())) { // if this is true then the game must be over
						extraTurn = false; // no more turns can be allowed
						anotherRound = false;
					}
					
				} while(extraTurn);
				
				if(anotherRound) { // makes sure the game isn't over before starting the opponent's turn
					System.out.println();
					System.out.println("----- Opponent's Turn -----");
					do { // while the opponent receives extra turns
						extraTurn = false;
						System.out.println();
						int [] countArray = oHand.countHand(); // the amounts of each rank in the opponent's hand are found
						int [] countBackup = oHand.countHand(); // countArray will be ordered by insertion sort, so we back up the unordered original
						int [] orderedRanks = insertionSort(countArray); // insertion sort returns the ranks in the order of how many the opponent has
						boolean remembered = false; // to keep track of if a rank is confirmed to have been recently asked for
						int j = 8; // index for orderedRanks, begins with the rank of which the opponent has most of
						do { // while a suitable rank to ask for has not been found
							remembered = false;
							for(int i = 0; i < 3; i++) { // checks through opponentMemory to see if the rank they want was recently asked for
								if(orderedRanks[j] == opponentMemory[i]) {
									remembered = true;
									j--; // if so, try again for the next best rank
								}
							}
						} while(remembered && countBackup[orderedRanks[j]-2] != 0); // stops looking if the rank has not been recently asked, or if they have exhausted their options
						
						int rankAsk;
						if(countBackup[orderedRanks[j]-2] == 0) { // if they can only ask for something they remember, then resort to asking for the rank they have most of
							rankAsk = orderedRanks[8];
						} else rankAsk = orderedRanks[j]; // if a suitable rank that was not recently asked was found, ask for that one
						
						System.out.println("Opponent: 'Do you have any " + rankAsk + "s?'");
						int [] ourCountArray = pHand.countHand(); // amounts of each rank in OUR hand our found
						if(ourCountArray[rankAsk-2] != 0) { // rankAsk-2 is the index of the number of cards of rank rankAsk in our hand
							extraTurn = true;
							for(int i = 0; i < 3; i++) { // if we have 1-3 rankAsk, then take it from our hand and insert it into opponent's hand
								oHand.insertCard(pHand.delete(rankAsk));
							}
							System.out.println("It's a catch! Opponent has taken all of your " + rankAsk + "s!");
							
							int bookCheck = oHand.checkForBook(); // checks if a book has been collected
							if(bookCheck != 0) {
								System.out.println("Opponent has completed the book of " + bookCheck + "s!");
								oScore += 1; // increments opponent's score
							}
							System.out.println("Opponent may now take an extra turn.");
						} else { // if we had none
							System.out.println("You: 'Go Fish!'");
							
							Link check = deck.drawCard(); // the card is stored in check so we can check if it is a lucky catch
							oHand.insertCard(check); // the card is inserted into the opponent's hand
							if(check != null) System.out.println("Opponent has drawn a card from the deck."); // will only be false if the deck was empty
							
							int bookCheck = oHand.checkForBook(); // checks if a book has been collected
							if(bookCheck != 0) {
								System.out.println("Opponent has completed the book of " + bookCheck + "s!");
								oScore += 1;
							}
							if(check != null && check.data == rankAsk) { // checks for a lucky catch
								extraTurn = true;
								System.out.println("A lucky catch! Opponent drew exactly what they wanted, so they may take an extra turn.");
							}
						}
						
						if(oHand.isEmpty()) { // if the hand is empty new cards can be drawn
							int noOfNewCards = oHand.drawNewHand(deck);
							if(noOfNewCards != 0) {
								System.out.println("Opponent has emptied their hand and have drawn " + noOfNewCards + " new cards from the deck.");
								
								int bookCheck = oHand.checkForBook(); // checks if a book has been collected
								if(bookCheck != 0) {
									System.out.println("Opponent has completed the book of " + bookCheck + "s!");
									oScore += 1; // increments opponent's score
								}
							} else System.out.println("Opponent has emptied their hand."); // if the deck was empty and 0 new cards were drawn
						}
						if(pHand.isEmpty()) { // if the hand is empty new cards can be drawn
							int noOfNewCards = pHand.drawNewHand(deck);
							if(noOfNewCards != 0) {
								System.out.println("You have emptied your hand and have drawn " + noOfNewCards + " new cards from the deck.");
								
								int bookCheck= pHand.checkForBook(); // checks if a book has been collected
								if(bookCheck != 0) {
									System.out.println("You have completed the book of " + bookCheck + "s!");
									pScore += 1;  // increments player's score
								}
							} else System.out.println("You have emptied your hand.");  // if the deck was empty and 0 new cards were drawn
						}
						
						opponentMemory[opponentMemoryIndex] = rankAsk; // stores the rank the opponent asked for on this turn in their memory
						opponentMemoryIndex++; // increment index so the next one will go to the next slot
						if(opponentMemoryIndex == 3) opponentMemoryIndex = 0; // if they've reached the end of their memory, start writing over it
						
						if((pHand.isEmpty() && deck.isEmpty()) || (oHand.isEmpty() && deck.isEmpty())) {  // if this is true then the game must be over
							extraTurn = false; // no more turns can be allowed
							anotherRound = false;
						}
						
					} while(extraTurn);
				}
				
				System.out.println();
				System.out.println("SCORE: Player " + pScore + " | " + oScore + " Opponent"); // between each pair of turns the score is printed
				
			} while(anotherRound); // if the game is not over, continue to player's next turn
			
			if(pScore > oScore) { // player win condition
				System.out.println("With " + pScore + " books, YOU WIN!");
			} else System.out.println("YOU LOSE");
			
			System.out.println("Would you like to play again?"); // asks if the player would like to play another game
			sc.nextLine(); // eats extra line caused by .nextInt above
			String input = new String(sc.nextLine());
			if(input.equals("yes") || input.equals("Yes")) { // the player can respond with yes, otherwise the program will terminate
				replay = true;
			}
		} while(replay); // returns to the beginning of a new game
		sc.close();
		System.out.println("Thanks for playing!");
	}
	
	// 		the shuffle method creates and fills an array "deckArray" with
	// cards in order, then performs several swaps to shuffle them and 
	// finally puts them into a linked list
	public static LinkedList shuffle() {
		int [] deckArray = new int [36];
		int pos = 0;
		for(int i = 2; i <= 10; i++) { // this nested loop adds cards of rank 2-10 to the array
			for(int j = 0; j < 4; j++) {
				deckArray[pos] = i;
				pos++;
			}
		}
		
		int temp;
		for(int i = 0; i < 36; i++) { // this loop performs 36 swaps to shuffle the array's contents
			pos = (int)(Math.random()*36); // generates a random position for the card
			temp = deckArray[pos];
			deckArray[pos] = deckArray[i];
			deckArray[i] = temp;
		}
		
		LinkedList deck = new LinkedList();
		for(int i = 0; i < 36; i++) { // the array's contents are copied to a linked list "deck"
			deck.createCard(deckArray[i]); // the createCard method converts each part of the array into their link equivalents
		}
		
		return deck; // the linked list is returned to the main method
	}
	
	// insertion sort used to order the ranks by how many are in a hand
	// it sorts the countArray (entered through parameter) of the hand and performs identical operations to the array "ranks"
	public static int[] insertionSort(int[] array) {
		int [] ranks = {2,3,4,5,6,7,8,9,10};
		for(int outer = 1; outer < 9; outer++) {
			int temp = array[outer];
			int ranksTemp = ranks[outer]; // performs the same backup for ranks
			int inner = outer;
			while(inner > 0 && array[inner - 1] >= temp) {
				array[inner] = array[inner - 1];
				ranks[inner] = ranks[inner - 1]; // performs the same swap to ranks
				inner--;
			}
			array[inner] = temp;
			ranks[inner] = ranksTemp; // performs the same insertion to ranks
		}
		return ranks; // returns the ranks ordered by how many are in a hand
	}
	
}

// the LinkedList class constructs the deck list and the hands of the players
// it contains many methods to perform different operations on the hands throughout the game
class LinkedList {
	public Link first; // first link of the list
	
	public LinkedList() { // constructor
		first = null;
	}
	
	// returns true if the linked list has no cards in it
	public boolean isEmpty() {
		return (first == null);
	}
	
	// pushes a card onto a stack (the deck list)
	// it is used to covert deckArray's contents into links for the deck list
	public void createCard(int cardRank) {
		Link newLink = new Link(cardRank);
		newLink.next = first;
		first = newLink;
	}
	
	// an insertOrdered method
	// used for adding cards to hands neatly
	public void insertCard(Link newLink) {
		if(newLink != null) {
			Link previous = null;
			Link current = first;
			
			while(current != null && newLink.data > current.data) { // finds the right spot for the card
				previous = current;
				current = current.next;
			}
		
			if(previous == null) { 
				first = newLink;
			} else {
				previous.next = newLink;
			}
			newLink.next = current; // adds it in
		}
	}
	
	// a standard deleteHead() method for a linked list
	public Link drawCard() {
		if(!isEmpty()) {
			Link temp = first; // takes card from top of deck stack
			first = first.next;
			return temp;
		} else { // if the deck is empty then:
			System.out.println("The deck is empty so no card is drawn.");
			return null;
		}
	}
	
	// a standard delete() method for a linked list
	public Link delete(int key) {
		Link current = first;
		Link previous = first;
		if(isEmpty()) { // if list is empty
			return null;
		} else { // if list is not empty
			while(current.data != key) { // finds the desired card
				if(current.next == null) { // if its not there
					return null;
				} else { // checks next card
					previous = current;
					current = current.next;
				}
			}
			
			if(current == first) { // deletes the card
				first = first.next;
			} else {
				previous.next = current.next;
			}
		
			return current; // returns the card so it may be moved to other hands
		}
	}
	
	// shows the player their hand
	public void printHand() {
		System.out.print("Your hand: ");
		Link current = first;
		while(current != null) {
			System.out.print(current.data + ", "); // traverses the list, printing the rank of each card
			current = current.next;
		}
		System.out.println();
	}
	
	// counts the number of cards of each rank in a hand
	// used to see if a player is allowed to ask for their requested card rank (since they must have one to ask for one)
	// part of the process of how the computer opponent can make informed decisions on what rank is the best to ask for
	// is also called in the checkForBook() method
	public int[] countHand() {
		int [] countArray = new int [9]; // index 0 is the # of 2s, 1 the # of 3s... 8 the # of 10s in the hand
		Link current = first;
		
		if(!isEmpty()) {
			while(current.next != null) {
				countArray[current.data - 2]++; // checks the rank and increments the number of cards of that rank found in the hand, in the corresponding index
				current = current.next;
			}
			countArray[current.data - 2]++; // this is just for the last link
			return countArray; // an unordered array with the amount of cards of each rank in the hand is returned
		} else {
			int [] emptyHand = {0,0,0,0,0,0,0,0,0}; // this is returned in case of an empty hand
			return emptyHand;
		}
	}
	
	// after cards have been gained, this method checks if 4 of a kind are now together
	// it returns true if a book has been collected
	public int checkForBook() {
		int [] countArray = countHand(); // the countHand() method is called so this one can see if 4 of a kind are there
		int key = 0;
		for(int i = 0; i < 9; i++) {
			if(countArray[i] == 4) { // if there's 4 then a book is found
				key = i+2; // i+2 is the rank corresponding to index i of the countArray
			}
		}
		if(key != 0) {
			for(int i = 0; i < 4; i++) { // deletes the 4 cards from the hand (and the game)
				delete(key);
			}
			return key;
		} else return 0; // if key == 0 then no book is found
	}
	
	// when a players hand is emptied they can draw up to 7 more cards from the deck, providing the deck still has cards
	public int drawNewHand(LinkedList deck) {
		int count = 0;
		while(count < 7 && !deck.isEmpty()) {
			insertCard(deck.drawCard()); // draws cards until 7 or until the deck is empty
			count++;
		}
		return count; // returns the number of new cards
	}
}

// a standard link class. Each link holds a card
class Link {
	public int data; // stores the rank of the card
	public Link next; // points to next link
	
	public Link(int dataIn) {
		data = dataIn; // takes in the rank
	}
}