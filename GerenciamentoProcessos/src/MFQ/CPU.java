package MFQ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CPU extends Thread{
	
	public static int cont;
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		ArrayList<Processo> processosHigh = new ArrayList<>();
		ArrayList<Processo> processosLow = new ArrayList<>();
		Random rand = new Random();
		
		System.out.println("Quantidade de processo para ser iniciado: ");
		int quant = scanner.nextInt();
		
		for (int i = 0; i < quant; i++) {
			Processo processo = new Processo(i, 1, processosHigh, processosLow,rand.nextInt(2));
			
			if (processo.getPrioridade() == 1) {
				processosHigh.add(processo);
			} else {
				processosLow.add(processo);
			}
		}
		
		try {
			System.out.println("\nPressione ENTER para continuar...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		int j = 0;
		int contAnteriorHigh = processosHigh.size();
		int contAnteriorLow = processosLow.size();
		int highPriority;
		
		while (processosHigh.size() != 0 || processosLow.size() != 0) {

			highPriority = 0;
			
			if (processosHigh.size() >= 1) {
				highPriority = 1;
			}
			
			
			if (highPriority == 1 && processosHigh.size() > 0) {
				try {
					processosHigh.get(i).setProcessIO(false);
					Thread.sleep(20);
					contAnteriorHigh = processosHigh.size();
					if (processosHigh.get(i).isAlive()) {
						System.out.println("Processo: " + processosHigh.get(i).getNumProcesso() + " de prioridade alta, foi escalonado para a CPU.");
						processosHigh.get(i).resume();
					} else {
						processosHigh.get(i).start();
					}
					
					cont = 1;		
					while (cont <= 10) {
						Thread.sleep(500);
						cont++;
					}
					
					if (processosHigh.size() == contAnteriorHigh && processosHigh.size() != 1) {	// Processos se mantém e existe mais um
						System.out.println("Processo: " + processosHigh.get(i).getNumProcesso() + " foi pausado e retirado da CPU.\n");			
						processosHigh.get(i).suspend();
						boolean troca = classificarProcesso(processosHigh.get(i), processosLow, processosHigh);
						
						if (troca) {
							i = i % processosHigh.size();
						} else {
							i = (i + 1) % processosHigh.size();
						}
						
					} else if (processosHigh.size() != contAnteriorHigh && i >= processosHigh.size()) {	// Processo removido e é o ultimo
						i = 0;
					}	// Processo removido e é o ultimo
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IllegalMonitorStateException e) {
					e.printStackTrace();
				}
			} else if (processosLow.size() > 0) {
				try {
					processosLow.get(i).setProcessIO(false);
					Thread.sleep(20);
					contAnteriorLow = processosLow.size();
					if (processosLow.get(j).isAlive()) {
						System.out.println("Processo: " + processosLow.get(j).getNumProcesso() + " de prioridade baixa, foi escalonado para a CPU.");
						processosLow.get(j).resume();
					} else {
						processosLow.get(j).start();
					}
					
					cont = 1;		
					while (cont <= 10) {
						Thread.sleep(500);
						cont++;
					}
							
					if (processosLow.size() == contAnteriorLow && processosLow.size() != 1) {	// Processos se mantém e existe apenas um
						System.out.println("Processo: " + processosLow.get(j).getNumProcesso() + " foi pausado e retirado da CPU.\n");
						processosLow.get(j).suspend();
						boolean troca = classificarProcesso(processosLow.get(j), processosLow, processosHigh);
						
						if (troca) {
							j = j % processosLow.size();
						} else {
							j = (j + 1) % processosLow.size();
						}
						
					} else if (processosLow.size() != contAnteriorLow && j >= processosLow.size()) {	// Processo removido e é o ultimo
						j = 0;
					} // Processo removido e é o ultimo
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (IllegalMonitorStateException e) {
					e.printStackTrace();
				}
			}
			
		}

		scanner.close();
	}
	
	private boolean classificarProcesso(Processo p, ArrayList<Processo> processosLow, ArrayList<Processo> processosHigh) {
		boolean troca = false;
		if (p.isProcessIO()) {
			p.setContProcessIO(p.getContProcessIO() + 1);
		} else {
			p.setContProcessCPU(p.getContProcessCPU() + 1);
		}
		
		if ((double)(p.getContProcessIO()/
				(p.getContProcessIO() + p.getContProcessCPU())) >= 0.5) {
			
			p.setPrioridade(1);
			if (processosLow.contains(p)) {
				System.out.println("Processo: " + p.getNumProcesso() + " agora é um processo de alta prioridade!\n");
				processosLow.remove(p);
				processosHigh.add(p);
				troca = true;
			}
		} else {
			p.setPrioridade(0);
			if (processosHigh.contains(p)) {
				System.out.println("Processo: " + p.getNumProcesso() + " agora é um processo de baixa prioridade!\n");
				processosHigh.remove(p);
				processosLow.add(p);
				troca = true;
			}
		}
		
		return troca;
	}
	
}
