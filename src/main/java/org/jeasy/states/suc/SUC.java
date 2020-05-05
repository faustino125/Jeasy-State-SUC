/*
 * The MIT License
 *
 * Copyright 2020 FAUSTINO.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jeasy.states.suc;

import java.util.*;
import org.jeasy.states.api.FiniteStateMachine;
import org.jeasy.states.api.FiniteStateMachineException;
import org.jeasy.states.api.State;
import org.jeasy.states.api.Transition;
import org.jeasy.states.core.FiniteStateMachineBuilder;
import org.jeasy.states.core.TransitionBuilder;
import org.jeasy.states.suc.actividades.Avanzar;
import org.jeasy.states.suc.actividades.Retroceder;
import org.jeasy.states.suc.estados.MaquetaAjustar;
import org.jeasy.states.suc.estados.MaquetaAprobar;
import org.jeasy.states.suc.estados.MaquetaCumple;
import org.jeasy.states.suc.estados.MaquetaRevisar;

/**
 *
 * @author FAUSTINO
 */
public class SUC {
    public static void main(String[] args) throws FiniteStateMachineException {
        //Definir los estados
        State maquetaCrear=new State("maquetaCrear");
        State maquetaRevisar=new State("maquetaRevisar");
        State maquetaCumple=new State("maquetaCumple");//condicion
        State maquetaAjustar=new State("maquetaAjustar");
        State maquetaAprobar=new State("maquetaAprobar");
        
        Set<State> estados=new HashSet<>();
        estados.add(maquetaCrear);
        estados.add(maquetaRevisar);
        estados.add(maquetaCumple);
        estados.add(maquetaAjustar);
        estados.add(maquetaAprobar);

        /* DEFINIR TRANSICIONES */
        /*---- M1 ----*/
        // 1
        Transition revisarMaqueta = new TransitionBuilder()
                .name("revisarMaqueta") 
                .sourceState(maquetaCrear)
                .eventType(Avanzar.class) // y el evento crear ocurre  
                .eventHandler(new MaquetaRevisar()) // debemos realizar las acciones revisar
                .targetState(maquetaRevisar) // y hacer una transición al estado revisar
                .build();
        // 0
        Transition crearMaqueta=new TransitionBuilder()
                .name("crearMaqueta")
                .sourceState(maquetaCrear)
                .eventType(Retroceder.class)
                .targetState(maquetaCrear)
                .build();
        /*---- M2 ----*/
        // 1
        Transition cumpleMaqueta=new TransitionBuilder()
                .name("cumpleMaqueta")
                .sourceState(maquetaRevisar)
                .eventType(Avanzar.class)
                .eventHandler(new MaquetaCumple())
                .targetState(maquetaCumple)
                .build();
        // 0
        Transition revisarMaquetas=new TransitionBuilder()
                .name("revisarMaquetas")
                .sourceState(maquetaRevisar)
                .eventType(Retroceder.class)
                .targetState(maquetaRevisar)
                .build();
        /*---- M3 ----*/
        // 1
        Transition aprobarMaqueta=new TransitionBuilder()
                .name("aprobarMaqueta")
                .sourceState(maquetaCumple)
                .eventType(Avanzar.class)
                .eventHandler(new MaquetaAprobar())
                .targetState(maquetaAprobar)
                .build();
        // 0
        Transition ajusteMaqueta=new TransitionBuilder()
                .name("ajusteMaqueta")
                .sourceState(maquetaCumple)
                .eventType(Retroceder.class)
                .eventHandler(new MaquetaAjustar())
                .targetState(maquetaAjustar)
                .build();
        /*---- M4 ----*/
        // 1 
        Transition reintentar=new TransitionBuilder()
                .name("reintentar")
                .sourceState(maquetaAjustar)
                .eventType(Avanzar.class)
                .eventHandler(new MaquetaCumple())
                .targetState(maquetaCumple)
                .build();
        // 0
        Transition ningunoProceso=new TransitionBuilder()
                .name("ningunoProceso")
                .sourceState(maquetaAjustar)
                .eventType(Retroceder.class)
                .targetState(maquetaAjustar)
                .build();
        /*---- M5 ----*/
        // 1
        Transition procesoAprobar=new TransitionBuilder()
                .name("procesoAprobar")
                .sourceState(maquetaAprobar)
                .eventType(Avanzar.class)
                .targetState(maquetaAprobar)
                .build();
        // 0
        /* CONSTRUIR INSTANCIAS */  
        FiniteStateMachine estadosSUC=new FiniteStateMachineBuilder(estados, maquetaCrear)
            .registerTransition(revisarMaqueta)
            .registerTransition(crearMaqueta)
                
            .registerTransition(cumpleMaqueta)
            .registerTransition(revisarMaquetas)
                
            .registerTransition(aprobarMaqueta)
            .registerTransition(ajusteMaqueta)
            
            .registerTransition(reintentar)
            .registerTransition(ningunoProceso)
                
            .registerTransition(procesoAprobar)
            .registerTransition(ningunoProceso)
            .build();
        
        // Disparar eventos de los estados SUC
        
        System.out.println("Estado inicial de SUC: "+estadosSUC.getCurrentState().getName());
        
        Scanner scanner=new Scanner(System.in);
        System.out.println("1 AVANZAR");
        System.out.println("0 RETROCEDER");
        System.out.println("X Salir");
        while (true) {            
            String entrada=scanner.nextLine();
            if(entrada.trim().equalsIgnoreCase("1")){
                System.out.println("Actividad realizado...");
                estadosSUC.fire(new Avanzar());
                System.out.println("Estado: "+estadosSUC.getCurrentState().getName());
                System.out.println("1 Avanzar o Retroceder");
            }
            if(entrada.trim().equalsIgnoreCase("0")){
                System.out.println("Actividad realizado...");
                estadosSUC.fire(new Retroceder());
                System.out.println("Estado: "+estadosSUC.getCurrentState().getName()); 
                System.out.println("1 Avanzar o Retroceder");
            }
            if(entrada.trim().equalsIgnoreCase("X")){
                System.out.println("Saliendo...");
                System.exit(0);
            }
        }

        
    }
}
