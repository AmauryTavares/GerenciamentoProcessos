package AlternanciaEstrita;

import java.util.ArrayList;
import java.util.Random;

public class Processo extends Thread{
	private int numProcesso;
	private static Integer valorCompartilhado;
	private int valorLimitante;		//Delimita o fim do processo por determinado valor da variável compartilhada
	private static Integer vez;
	private ArrayList<Processo> processos;
	
	public Processo(int numProcesso, Integer valorCompartilhadoInicial, Integer vezInicial, ArrayList<Processo> processos) {
		this.numProcesso = numProcesso;
		valorCompartilhado = valorCompartilhadoInicial;
		this.processos = processos;
		vez = vezInicial;
		iniciarValorLimitante();
		System.out.println("Processo: " + this.numProcesso + " criado, valor limitante: " + this.valorLimitante + ".");
	}
	
	private void iniciarValorLimitante () {
		Random rand = new Random();
		this.valorLimitante = rand.nextInt(50) + 20;
	}
	
	@Override
	public void run() {
		boolean setVez = true;
		
		System.out.println("Processo: " + this.numProcesso + " iniciado!");
		
		while (valorCompartilhado <= this.valorLimitante) {
			System.out.println("Processo: " + this.numProcesso + " tenta acessar o valor compartilhado.");
			
			boolean trava = true;
			int cont = 0;
			int fimCont = 0;
			
			while (trava) {
				try {
					sleep(20);
					
					entrarNaRegiaoCritica();
						
					if (valorCompartilhado <= this.valorLimitante) {
						System.out.println("Processo: " + this.numProcesso + " conseguiu acessar o valor compartilhado.");
						
						cont = 0;
						Random rand = new Random();
						fimCont = rand.nextInt(9) + 1;
						
						while (cont != fimCont && valorCompartilhado <= this.valorLimitante) {
							sleep(800);
							System.out.println("Valor compartilhado: " + valorCompartilhado++);
							cont+=1;
						}		
						
						trava = false;
					} else {
						trava = false;
					}
					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			
			sairDaRegiaoCritica();
			
			setVez = false;
			
			System.out.println("### Vez: " + vez + " ###");
			System.out.println("Processo: " + this.numProcesso + " liberou o valor compartilhado.");
			
		}
		
		if (setVez) {
			sairDaRegiaoCritica();
			System.out.println("### Vez: " + vez + " ###");
		}
		
		
		processos.remove(this);
		System.out.println("Processo: " + this.numProcesso + " encerrou!");
		
	}

	public int getNumProcesso() {
		return numProcesso;
	}
	
	private void sairDaRegiaoCritica() {
		for (int i = 0; i < this.processos.size(); i++) {
			if (this.processos.get(i).getNumProcesso() == this.numProcesso) {
				if (this.processos.size() > i+1) {
					vez = this.processos.get(i+1).getNumProcesso();
				} else {
					vez = this.processos.get(0).getNumProcesso();
				}
			}
		}
	}
	
	private void entrarNaRegiaoCritica() {
		while (vez != this.numProcesso) {	
			try {
				sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
