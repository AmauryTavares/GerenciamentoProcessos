package Semaforo;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Processo extends Thread{

	private int numProcesso;
	private static Integer valorCompartilhado;
	private int valorLimitante;		//Delimita o fim do processo por determinado valor da variável compartilhada
	private static Semaphore sem = new Semaphore(1);	// mutex
	private ArrayList<Processo> processos;
	
	public Processo(int numProcesso, Integer valorCompartilhadoInicial, ArrayList<Processo> processos) {
		this.numProcesso = numProcesso;
		valorCompartilhado = valorCompartilhadoInicial;
		this.processos = processos;
		iniciarValorLimitante();
		System.out.println("Processo: " + this.numProcesso + " criado, valor limitante: " + this.valorLimitante + ".");
	}
	
	private void iniciarValorLimitante () {
		Random rand = new Random();
		this.valorLimitante = rand.nextInt(50) + 20;
	}
	
	@Override
	public void run() {
		System.out.println("Processo: " + this.numProcesso + " iniciado!");
		
		while (valorCompartilhado <= this.valorLimitante) {
			
			int cont;
			int fimCont;
			cont = 0;
			Random rand = new Random();
			fimCont = rand.nextInt(9) + 1;
						
			try {
				System.out.println("Processo: " + this.numProcesso + " tenta acessar o valor compartilhado.");
				
				entrarNaRegiaoCritica();

				System.out.println("Processo: " + this.numProcesso + " conseguiu acessar o valor compartilhado.");
				while (cont != fimCont && valorCompartilhado <= this.valorLimitante) {
					sleep(800);
					System.out.println("Valor compartilhado: " + valorCompartilhado++);
					cont+=1;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				sairDaRegiaoCritica();
			}	

			System.out.println("Processo: " + this.numProcesso + " liberou o valor compartilhado.");
			
			try {
				System.out.println("Processo: " + this.numProcesso + " processando com CPU...");
				sleep(rand.nextInt(3000) + 2000);
				System.out.println("Processo: " + this.numProcesso + " terminou o processamento com CPU.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.processos.remove(this);
		System.out.println("Processo: " + this.numProcesso + " encerrou!\n");
		
	}

	public int getNumProcesso() {
		return numProcesso;
	}
	
	private void entrarNaRegiaoCritica() {
		boolean loop = true;
		
		try {
			while (loop) {
				sleep(20);
				if (!sem.tryAcquire()) {
					CPU.cont = 10; 	// Solicita a troca de processo
				} else {
					loop = false;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void sairDaRegiaoCritica() {
		sem.release();
	}
	
}
