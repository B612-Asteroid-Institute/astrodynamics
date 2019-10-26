package org.b612foundation.adam.estimators;

import org.b612foundation.adam.datamodel.PropagatorConfiguration;
import org.b612foundation.adam.datamodel.estimation.*;
import org.b612foundation.adam.opm.OorbToOdmConverter;
import org.b612foundation.adam.opm.OrbitParameterMessage;
import org.b612foundation.adam.runnable.AdamRunnableException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.b612foundation.adam.datamodel.estimation.OrbitDeterminationParameters.MeasurumentType.*;

public class OpenOrbEstimator implements OrbitEstimator {
    private static Logger log = Logger.getLogger(OpenOrbEstimator.class.getName());
    private Path baseDirectory;

    public OpenOrbEstimator() throws IOException {
        this(Files.createTempDirectory("oorb_od"));
    }

    public OpenOrbEstimator(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    @Override
    public OrbitParameterMessage batchEstimate(OrbitDeterminationParameters odParams,
                                               OrbitDeterminationConfiguration odConfig,
                                               PropagatorConfiguration propConfig,
                                               String propagationIdForLogging) throws AdamRunnableException, IOException {

        Path obsFilePath = Paths.get(baseDirectory.toString(), "od.des");
        if(odParams.getMeasurementsFormatType().equals(LsstCsvFilePath.toString())) {
            File inputObsPath = Paths.get(odParams.getMeasurements()).toFile();
            if(!inputObsPath.exists()) {
                throw new AdamRunnableException("Input obs file not found: " + inputObsPath, null);
            }

            if(!inputObsPath.canRead()) {
                throw new AdamRunnableException("Input obs file can't be read: " + inputObsPath, null);
            }

            List<LsstMeasurement> inputMeasurements = LsstMeasurementReader.readSingleObjectCsvFile(
                    inputObsPath.toPath(), "I11"); //TODO: this will be in the LSST measurement in the next release

            new OorbMeasurementWriter().writeLsstMeasurementsToDesFile(obsFilePath, inputMeasurements);
        } else {
            throw new AdamRunnableException("Orbit Measurement type is not supported: "
                    + odParams.getMeasurementsFormatType(), null);
        }

        Path configFile = Paths.get(baseDirectory.toString(),"oorb.conf");
        OorbConfigWriter.writeConfiguration(configFile, odConfig, propConfig);

        Path odOrbitOutput = Paths.get(baseDirectory.toString(), "fullOd_orbit.orb");
        if (odParams.getType() == OrbitDeterminationParameters.OdType.Initial) {
            Path iodOrbitOutput = Paths.get(baseDirectory.toString(), "iod_orbit.orb");
            runInitialOD(configFile, obsFilePath, iodOrbitOutput);
            runFullOd(configFile, obsFilePath, iodOrbitOutput, odOrbitOutput);
        } else {
            Path inputOrbFile = Paths.get(baseDirectory.toString(),"init_orbit.orb");
            OorbToOdmConverter.opmToOorbFile(odParams.getInitialStateEstimate(), 20, 0.00005, inputOrbFile);
            runFullOd(configFile, obsFilePath, inputOrbFile, odOrbitOutput);
        }
        OrbitParameterMessage opm = OorbToOdmConverter.oorbFullOrbitToOpm(getStateVectorLine(odOrbitOutput));
        return opm;
    }

    private void runFullOd(Path configFile, Path obsInFile, Path orbInPath, Path orbOutPath) throws IOException, AdamRunnableException {
        String[] cmds = {
                buildExecPath(),
                "--task=lsl",
                "--conf=" + configFile.toFile().getAbsolutePath(),
                "--obs-in=" + obsInFile.toFile().getAbsolutePath(),
                "--orb-in=" + orbInPath.toFile().getAbsolutePath(),
                "--orb-out=" + orbOutPath.toFile().getAbsolutePath()
        };

        runOorb(cmds);
    }

    private void runInitialOD(Path configFile, Path obsInFile, Path orbOutPath) throws IOException, AdamRunnableException {
        String[] cmds = {
                buildExecPath(),
                "--task=ranging",
                "--conf=" + configFile.toFile().getAbsolutePath(),
                "--obs-in=" + obsInFile.toFile().getAbsolutePath(),
                "--orb-out=" + orbOutPath.toFile().getAbsolutePath()
        };

        runOorb(cmds);
    }

    private String buildExecPath() {
        String base = getOorbBasePath();
        String execPath = Paths.get(base, "main/oorb").toString();

        return execPath;
    }

    private void runOorb(String[] cmds) throws IOException, AdamRunnableException {
        Process process = Runtime.getRuntime().exec(cmds);
        String response = "";
        try (InputStream input = process.getInputStream(); InputStream error = process.getErrorStream()) {
            response = response + new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"))
                    + "\n";
            String errors = new BufferedReader(new InputStreamReader(error)).lines().collect(Collectors.joining("\n"))
                    + "\n";
            if (!errors.trim().isEmpty()) {
                System.out.println("ERRORS [" + errors + "]");
                throw new AdamRunnableException("Oorb error: " + errors, null);
            }
        }
    }

    private String getOorbBasePath() {
        String var = System.getenv("OORBROOT");
        if (var != null) {
            return var;
        }
        String current = System.getProperty("user.dir");
        log.info("OpenOrb root var OORBROOT is not found, assuming current directory " + current);
        return current;
    }

    private String getStateVectorLine(Path orbFile) throws IOException {
        List<String> lines = Files.readAllLines(orbFile)
                .stream()
                .filter(s -> !s.trim().startsWith("#"))
                .collect(Collectors.toList());
        return lines.get(0);
    }

}
