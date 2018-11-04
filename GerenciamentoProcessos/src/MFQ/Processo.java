package MFQ;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Processo extends Thread{
	private int numProcesso;
	private static Integer valorCompartilhado;
	private int valorLimitante;		//Delimita o fim do processo por determinado valor da variável compartilhada
	private static Semaphore sem = new Semaphore(1);	// mutex
	private ArrayList<Processo> processosHigh;
	private ArrayList<Processo> processosLow;
	private int prioridade;
	private boolean ProcessIO = false;
	private int contProcessCPU = 0;
	private int contProcessIO = 0;
	
	public Processo(int numProcesso, Integer valorCompartilhadoInicial, ArrayList<Processo> processosHigh, ArrayList<Processo> processosLow, int prioridade) {
		this.numProcesso = numProcesso;
		valorCompartilhado = valorCompartilhadoInicial;
		this.processosHigh = processosHigh;
		this.processosLow = processosLow;
		this.prioridade = prioridade;
		iniciarValorLimitante();
		System.out.println("Processo: " + this.numProcesso + " de prioridade " + (this.prioridade == 1 ? "alta" : "baixa") +
					" foi criado, valor limitante: " + this.valorLimitante + ".");
	}
	
	private void iniciarValorLimitante () {
		Random rand = new Random();
		this.valorLimitante = rand.nextInt(50) + 20;
	}
	
	@Override
	public void run() {
		System.out.println("Processo: " + this.numProcesso + " de prioridade " + 
	(this.prioridade == 1 ? "alta" : "baixa") + ", foi iniciado!");
		
		while (valorCompartilhado <= this.valorLimitante) {
			
			Random rand = new Random();
			try {
				System.out.println("Processo: " + this.numProcesso + " processando com CPU...");
				sleep(rand.nextInt(7000) + 2000);
				System.out.println("Processo: " + this.numProcesso + " terminou o processamento com CPU.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int cont;
			int fimCont;
			cont = 0;
			fimCont = rand.nextInt(9) + 1;
						
			try {
				System.out.println("Processo: " + this.numProcesso + " tenta acessar o valor compartilhado.");
				
				entrarNaRegiaoCritica();

				System.out.println("Processo: " + this.numProcesso + " conseguiu acessar o valor compartilhado.");
				while (cont != fimCont && valorCompartilhado <= this.valorLimitante) {
					ProcessIO = true;
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
				sleep(rand.nextInt(5000) + 2000);
				System.out.println("Processo: " + this.numProcesso + " terminou o processamento com CPU.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (processosHigh.contains(this)) {
			this.processosHigh.remove(this);
		} else {
			this.processosLow.remove(this);
		}
		
		System.out.println("Processo: " + this.numProcesso + " encerrou!\n");
		
	}
	
	private void entrarNaRegiaoCritica() {
		try {
			boolean loop = true;
			while (loop) {
				ProcessIO = true;
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
	
	public int getNumProcesso() {
		return numProcesso;
	}
	
	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public int getContProcessCPU() {
		return contProcessCPU;
	}

	public void setContProcessCPU(int contProcessCPU) {
		this.contProcessCPU = contProcessCPU;
	}

	public int getContProcessIO() {
		return contProcessIO;
	}

	public void setContProcessIO(int contProcessIO) {
		this.contProcessIO = contProcessIO;
	}

	public boolean isProcessIO() {
		return ProcessIO;
	}

	public void setProcessIO(boolean processIO) {
		ProcessIO = processIO;
	}
	
}
