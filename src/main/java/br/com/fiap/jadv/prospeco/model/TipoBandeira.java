package br.com.fiap.jadv.prospeco.model;

/**
 * <h1>TipoBandeira</h1>
 * Enumeração que define os tipos possíveis de bandeiras tarifárias,
 * conforme estabelecido pela ANEEL.
 *
 * <ul>
 *   <li>VERDE: Sem acréscimo na tarifa.</li>
 *   <li>AMARELA: Acréscimo moderado na tarifa.</li>
 *   <li>VERMELHA_1: Primeiro patamar de acréscimo na tarifa.</li>
 *   <li>VERMELHA_2: Segundo patamar de acréscimo na tarifa.</li>
 * </ul>
 *
 */
public enum TipoBandeira {
    VERDE,
    AMARELA,
    VERMELHA_1,
    VERMELHA_2
}
