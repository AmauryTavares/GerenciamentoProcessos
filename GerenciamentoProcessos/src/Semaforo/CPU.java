package Semaforo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CPU extends Thread{
	
	public static int cont;
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Quantidade de processo para ser iniciado: ");
		int quant = scanner.nextInt();

		ArrayList<Processo> processos = new ArrayList<>();
		
		for (int i = 0; i < quant; i++) {
			Processo processo = new Processo(i, 1, processos);
			processos.add(processo);
		}
		
		try {
			System.out.println("\nPressione ENTER para continuar...");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		int contAnterior = processos.size();
		
		while (contAnterior != 0) {
			try {
				Thread.sleep(20);
				contAnterior = processos.size();
				if (processos.get(i).isAlive()) {
					System.out.println("Processo: " + processos.get(i).getNumProcesso() + " foi escalonado para a CPU.");
					processos.get(i).resume();
				} else {
					processos.get(i).start();
				}
				
				if (processos.size() == 1) {	// evita que a cpu fique trocando quando há apenas um processo
					processos.remove(i);
				}
				
				cont = 1;		
				while (cont <= 10) {
					Thread.sleep(500);
					cont++;
				}
						
				if (processos.size() == contAnterior && processos.size() != 1) {	// Processos se mantém e existe mais de um
					System.out.println("Processo: " + processos.get(i).getNumProcesso() + " foi pausado e retirado da CPU.\n");
					processos.get(i).suspend();
					i = (i + 1) % processos.size();
				} else if (processos.size() != contAnterior && i >= processos.size()) {	// Processo removido e é o ultimo
					i = 0;
				}	// Processo removido e é o ultimo
				
				contAnterior = processos.size();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalMonitorStateException e) {
				e.printStackTrace();
			}
		}

		scanner.close();
	}
}
