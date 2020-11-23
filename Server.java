import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
public class Server {
    private static final Charset UTF8 = Charset.forName("UTF-8");
	static Map<String, Client> map =new HashMap<>();



	public static boolean CheckID(String id) {
		/**
		 *parse id, name
		 *check id is contain
		 *check name is contain
		 *if true == write ip with check ok
		 *if false == return false
		 * */
		return true;
	}
	
	public static boolean WriteDB(int id, String name) {
		/**
		 *parse id, name
		 *check id is contain
		 *check name is contain
		 *if true == write ip with check ok
		 *if false == return false
		 * */
		return true;
	}
	
	
	public static void ProblemStart(SocketChannel socket) throws IOException, InterruptedException {
		

			Random rand = new Random();
			int first = rand.nextInt(10);
			int second = rand.nextInt(10);
			String cal = String.format("%d + %d = ?", first, second);
			
			ByteBuffer buf = writebuf(cal, socket);
			String studentResult = readbuf(buf, socket);
			int parseResult = Integer.parseInt(studentResult);
			
			if (parseResult == (first+second)) {
				writebuf("Correct", socket);
			}
			else {
				writebuf("WrongStartAgain", socket);
			}
}
	
	
	private static boolean  CheckAttendanceComplete(SocketChannel socket) throws IOException {
		String address = socket.getRemoteAddress().toString();
		if(checkIP(address)) {
			if(checkStudent(socket)) {
				return true;
			}
			else {
				return false;
			}
			
		}
		else {
			return false;
		}
		
		
	}

	private static boolean checkStudent(SocketChannel socket) {
		// TODO Auto-generated method stub
		return true;
	}

	private static boolean checkIP(String address) {
		// TODO Auto-generated method stub
		return true;
	}

	public static ByteBuffer writebuf(String s, SocketChannel client) throws IOException {
		CharBuffer chars = CharBuffer.allocate(256);
		chars.put(s+'\n');
		chars.flip();
		ByteBuffer buffer = UTF8.encode(chars);
		client.write(buffer);
		buffer.clear();
		return buffer;
	}
	
	public static String readbuf(ByteBuffer buf, SocketChannel client) throws IOException, InterruptedException {
		client.read(buf);
		buf.flip();

		ByteBuffer newbuf = ByteBuffer.allocate(256);
		for (int i = 0; i<buf.limit();i++) {
			Byte b = buf.get(i);
			if (b >=0 && b<30) {
				continue;
			}
			newbuf.put(b);
		}
		
		return new String(newbuf.array(), UTF8).trim();
		
		
	}
		
	
	
	public static void interactiveClient(SelectionKey key) {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		SocketChannel client = (SocketChannel) key.channel();
		String clientIp;
		try {
			//clientIp = client.getRemoteAddress().toString().split(":")[0].replace("/", "");
			clientIp = client.getRemoteAddress().toString().replace("/", "");
			Client student = map.get(clientIp);
			String result;
			result = readbuf(buffer, client);
		
			if(student.getSession() == -1) {
				//TODO  get student id/ password
				//TODO  session up
				if(student.getId()== -1 && Objects.equals(result, "attendance") == false) {
					ByteBuffer buf = writebuf("Wrong Command, Attendance First", client);
				}
				
				else if(student.getId()== -1 && student.getName() == null) {
					student.setId(0);
					ByteBuffer buf = writebuf("input ID:", client);
				}
				else if(student.getId()== 0 && student.getName() == null) {
					if(CheckID(result)) {
						try {
							int id = Integer.parseInt(result);
							student.setId(id);	
							ByteBuffer buf = writebuf("input Name:", client);}
						catch(java.lang.NumberFormatException e) {
							ByteBuffer buf = writebuf("Non ID Type Try Again", client);		
							student.setId(-1);	

						}
					}
					else {
						ByteBuffer buf = writebuf("NO Student Contains Try Again", client);
						student.setId(-1);	

					}				
				}
				else if(student.getName() == null) {
					student.setName(result);		
					student.setSession(student.getSession()+1);
					WriteDB(student.getId(), student.getName());
					ByteBuffer buf = writebuf("Correct Now Type problem", client);
				}	
			}
		
		
			else if(student.getSession() == 0) {
				//TODO give problem
				//TODO session up
				if(Objects.equals(result, "problem") == false && student.getProblem()==-1) {
					ByteBuffer buf = writebuf("Wrong Command, You can only type problem", client);
				}

				else if(student.getProblem() == -1) {
					Random rand = new Random();
					int first = rand.nextInt(100);
					int second = rand.nextInt(100);
					String cal = String.format("[Problem] %d + %d = ?", first, second);
					ByteBuffer buf = writebuf(cal, client);
					student.setProblem(first+second);	
				}
				else {
					try {
						int calResult = Integer.parseInt(result);
						if(student.getProblem() == calResult) {
							writebuf("Correct", client);
							student.setSession(student.getSession()+1);
						}
						else {
							writebuf("Wrong TryAgain", client);
						}
					}
					catch(Exception e){
						e.getStackTrace();
						writebuf("WrongNumberFormat Try Again", client);
						
					}
				}	
			}
			else if(student.getSession() >= 1) {
				writebuf("All Problem Pass You Can Now Communication Freely", client);
				student.setSession(student.getSession()+1);
				String cal = String.format("[%d] %s", student.getId(), result);
				System.out.println(cal);
			}
			return;
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			key.cancel();
			
		}
		catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			key.cancel();
		}
		catch(Exception e2) {
			e2.printStackTrace();
			try {
				client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			key.cancel();		
		}
		
	

	}
	
	
	
	public static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException{
		
		SocketChannel client = serverSocket.accept();
		if (client == null) {
			return;
		}
		client.configureBlocking(false);
		client.register(selector, SelectionKey.OP_READ);
		//String ip = client.getRemoteAddress().toString().split(":")[0].replace("/", "");
		String ip = client.getRemoteAddress().toString().replace("/", "");
		if(map.containsKey(ip)) {
			System.out.println("Again "+ip);
		}
		else {
			map.put(ip, new Client(ip));	
			System.out.println("new client connected..."+ip);
		}

		

		
		
	}

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		
		try {
			Selector selector = Selector.open();
			ServerSocketChannel ssc  = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress("0.0.0.0", 6666));
			ssc.configureBlocking(false);
			ssc.register(selector, SelectionKey.OP_ACCEPT);
			
			
			
			//serverSocket = new ServerSocket(6666);
			//serverSocket.setReuseAddress(true);
			DataInputStream din = null;
			
			while(true) {
				int num = selector.select();
				
				if(num == 0) {
					continue;
				}
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				while(it.hasNext()) {
					SelectionKey key  = it.next();
					
					if(key.isAcceptable()) {
						register(selector, ssc);
					}
					else if(key.isReadable()) {
						interactiveClient(key);	
					}
				}
				it.remove();
				
				
				
				
				
				
				
			}

		}
		catch(Exception e) {
			System.out.println(e);
			
		}

	}

}
