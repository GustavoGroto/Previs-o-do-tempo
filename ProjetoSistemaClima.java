import org.json.JSONObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProjetoSistemaClima{
 public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Digite o nome da cidade: ");
		String cidade = scanner.nextLine(); // Le cidade;
		try {
			String dadosClimaticos = getDadosClimaticos(cidade); //retorna um JSON

			// Código 1006 siginidica que a cidade não encontrada ler documentação
			if (dadosClimaticos.contains("\" code\":1006")){ // \" code\":1006 representa "code": 1006 formato JSON
	 			System.out.println("Localização não encontrada. Insira um local valido");
			}else{
			imprimirDadosClimaticos(dadosClimaticos);
			}
		} catch (Exception e){
				System.out.println(e.getMessage());
			}
	}
	
	public static String getDadosClimaticos(String cidade) throws Exception{
		String apiKey = Files.readString(Paths.get("api-key.txt")).trim();

		String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
		String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formataNomeCidade;
		HttpRequest request = HttpRequest.newBuilder() // Começa a contrução de uma solicitação HTTP
			.uri(URI.create(apiUrl)) // define o URI da solicitação HTTP
			.build();

			//criar objeto enviar solitação HTTP e receber resposta, para acessar o site de WeatherAPI
		HttpClient client = HttpClient.newHttpClient();

		 // Agora vamos enviar requisições HTTP e receber respostas HTTP, comunicar com o site da API Meteorologica
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		return response.body(); // retorna os dados da API
	}

	//metodo que imprimi os dados de forma organizada
	public static void imprimirDadosClimaticos(String dados){
		// System.out.println("Dados originais (JSON)Obtidos no site meteorológico" + dados);

		JSONObject dadosJson = new JSONObject(dados);
		JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

		//Extrai os dados da localização
		String cidade = dadosJson.getJSONObject("location").getString("name");
		String pais = dadosJson.getJSONObject("location").getString("country");

		//Extrai os dados adicionais
		
		String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");
		int umidade = informacoesMeteorologicas.getInt("humidity");
		float velocidadeVento = informacoesMeteorologicas.getFloat("wind_kph");
		float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");
		float sensacaoTermica = informacoesMeteorologicas.getFloat("feelslike_c");
		float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

		//extrai data e hora da API
		String dataHoraString = informacoesMeteorologicas.getString("last_updated");

		//imprimi
		System.out.println("Informações Meteorológias para " + cidade +", " + pais);
		System.out.println("Data e hora: " + dataHoraString);
		System.out.println("Temperatura Atual: " + temperaturaAtual + "°C");
		System.out.println("Sensação Térmica: " + sensacaoTermica + "°C");
		System.out.println("Condição do Tempo: " + condicaoTempo);
		System.out.println("umidade: " + umidade + "%");
		System.out.println("Velocidade do vento: " + velocidadeVento + "Km/h");
		System.out.println("Pressão atimosférica: " + pressaoAtmosferica + " mb");
	}
}