import { Connector } from "./connector";
import { Persona } from "./persona";
import { Prompt } from "./prompt";

export class GetPersonaResponse {
	persona: Persona;
    prompt: Prompt;
    lstConnector: Connector[];
}
